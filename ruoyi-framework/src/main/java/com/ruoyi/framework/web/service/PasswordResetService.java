package com.ruoyi.framework.web.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.PasswordResetBody;
import com.ruoyi.common.core.domain.model.PasswordResetCodeBody;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.CaptchaException;
import com.ruoyi.common.exception.user.CaptchaExpireException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;

/**
 * Password reset flow for users with a bound contact method.
 */
@Component
public class PasswordResetService
{
    private static final String CONTACT_EMAIL = "email";

    private static final String CONTACT_PHONE = "phone";

    private static final String CONFIG_MAIL_HOST = "sys.mail.smtp.host";

    private static final String CONFIG_MAIL_PORT = "sys.mail.smtp.port";

    private static final String CONFIG_MAIL_USERNAME = "sys.mail.smtp.username";

    private static final String CONFIG_MAIL_PASSWORD = "sys.mail.smtp.password";

    private static final String CONFIG_MAIL_FROM = "sys.mail.smtp.from";

    private static final String CONFIG_MAIL_AUTH = "sys.mail.smtp.auth";

    private static final String CONFIG_MAIL_SSL_ENABLE = "sys.mail.smtp.sslEnable";

    private static final String CONFIG_MAIL_STARTTLS_ENABLE = "sys.mail.smtp.starttlsEnable";

    private static final String CONFIG_MAIL_TIMEOUT = "sys.mail.smtp.timeout";

    private static final String GENERIC_SENT_MESSAGE = "如果账号与绑定邮箱匹配，验证码将发送到该邮箱";

    private static final String GENERIC_RESET_MESSAGE = "账号、邮箱或验证码不正确";

    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private SysPasswordService passwordService;

    public Map<String, Object> getResetConfig()
    {
        Map<String, Object> config = new LinkedHashMap<String, Object>();
        List<String> contactTypes = new ArrayList<String>();
        if (emailResetEnabled())
        {
            contactTypes.add(CONTACT_EMAIL);
        }
        if (smsResetEnabled())
        {
            contactTypes.add(CONTACT_PHONE);
        }
        config.put("contactTypes", contactTypes);
        config.put("passwordMinLength", UserConstants.PASSWORD_MIN_LENGTH);
        config.put("passwordMaxLength", UserConstants.PASSWORD_MAX_LENGTH);
        config.put("pwdChrtype", passwordCharType());
        config.put("codeExpirationMinutes", codeExpirationMinutes());
        config.put("resendIntervalSeconds", resendIntervalSeconds());
        return config;
    }

    public String sendResetCode(PasswordResetCodeBody body)
    {
        String username = trim(body.getUsername());
        String contactType = normalizeContactType(body.getContactType());
        String contact = trim(body.getContact());

        validateCaptcha(body.getCode(), body.getUuid());
        validateCodeRequest(username, contactType, contact);

        if (CONTACT_PHONE.equals(contactType))
        {
            throw new ServiceException("短信验证码服务未配置，请联系管理员");
        }

        MailConfig mailConfig = loadMailConfig();
        SysUser user = findValidMatchedUser(username, contactType, contact);
        if (user == null)
        {
            return GENERIC_SENT_MESSAGE;
        }

        String identity = identityKey(username, contactType, contact);
        String sendKey = CacheConstants.PWD_RESET_SEND_KEY + identity;
        if (redisCache.hasKey(sendKey))
        {
            throw new ServiceException("验证码发送过于频繁，请稍后再试");
        }

        String verifyCode = createVerifyCode();
        sendEmailCode(user, contact, verifyCode, mailConfig);

        int expirationMinutes = codeExpirationMinutes();
        redisCache.setCacheObject(CacheConstants.PWD_RESET_CODE_KEY + identity, verifyCode, expirationMinutes,
                TimeUnit.MINUTES);
        redisCache.setCacheObject(sendKey, "1", resendIntervalSeconds(), TimeUnit.SECONDS);
        redisCache.deleteObject(CacheConstants.PWD_RESET_ERR_CNT_KEY + identity);
        return GENERIC_SENT_MESSAGE;
    }

    public void resetPassword(PasswordResetBody body)
    {
        String username = trim(body.getUsername());
        String contactType = normalizeContactType(body.getContactType());
        String contact = trim(body.getContact());
        String verifyCode = trim(body.getVerifyCode());
        String newPassword = body.getNewPassword();

        validateResetRequest(username, contactType, contact, verifyCode);
        validateNewPassword(newPassword);

        SysUser user = findValidMatchedUser(username, contactType, contact);
        if (user == null)
        {
            throw new ServiceException(GENERIC_RESET_MESSAGE);
        }

        String identity = identityKey(username, contactType, contact);
        String codeKey = CacheConstants.PWD_RESET_CODE_KEY + identity;
        String errorKey = CacheConstants.PWD_RESET_ERR_CNT_KEY + identity;
        String cacheCode = redisCache.getCacheObject(codeKey);
        if (StringUtils.isEmpty(cacheCode))
        {
            throw new ServiceException("验证码已失效，请重新获取");
        }

        Integer retryCount = redisCache.getCacheObject(errorKey);
        if (retryCount != null && retryCount >= maxErrorCount())
        {
            redisCache.deleteObject(codeKey);
            throw new ServiceException("验证码错误次数过多，请重新获取");
        }

        if (!cacheCode.equalsIgnoreCase(verifyCode))
        {
            retryCount = retryCount == null ? 1 : retryCount + 1;
            if (retryCount >= maxErrorCount())
            {
                redisCache.deleteObject(codeKey);
                redisCache.deleteObject(errorKey);
                throw new ServiceException("验证码错误次数过多，请重新获取");
            }
            redisCache.setCacheObject(errorKey, retryCount, codeExpirationMinutes(), TimeUnit.MINUTES);
            throw new ServiceException(GENERIC_RESET_MESSAGE);
        }

        userService.resetUserPwd(user.getUserId(), SecurityUtils.encryptPassword(newPassword));
        passwordService.clearLoginRecordCache(username);
        redisCache.deleteObject(codeKey);
        redisCache.deleteObject(errorKey);
        redisCache.deleteObject(CacheConstants.PWD_RESET_SEND_KEY + identity);
    }

    private void validateCaptcha(String code, String uuid)
    {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (!captchaEnabled)
        {
            return;
        }
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        redisCache.deleteObject(verifyKey);
        if (StringUtils.isEmpty(code) || !code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }

    private void validateCodeRequest(String username, String contactType, String contact)
    {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(contact) || StringUtils.isEmpty(contactType))
        {
            throw new ServiceException("账号、联系方式和图形验证码不能为空");
        }
        if (!CONTACT_EMAIL.equals(contactType) && !CONTACT_PHONE.equals(contactType))
        {
            throw new ServiceException("不支持的找回方式");
        }
        if (CONTACT_EMAIL.equals(contactType) && !emailResetEnabled())
        {
            throw new ServiceException("邮箱找回密码未启用");
        }
        if (CONTACT_PHONE.equals(contactType) && !smsResetEnabled())
        {
            throw new ServiceException("手机找回密码暂未启用");
        }
    }

    private void validateResetRequest(String username, String contactType, String contact, String verifyCode)
    {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(contact) || StringUtils.isEmpty(contactType)
                || StringUtils.isEmpty(verifyCode))
        {
            throw new ServiceException("账号、邮箱和邮箱验证码不能为空");
        }
        if (!CONTACT_EMAIL.equals(contactType))
        {
            throw new ServiceException("当前仅支持邮箱找回密码");
        }
        if (!emailResetEnabled())
        {
            throw new ServiceException("邮箱找回密码未启用");
        }
    }

    private SysUser findValidMatchedUser(String username, String contactType, String contact)
    {
        SysUser user = userService.selectUserByUserName(username);
        if (user == null || UserStatus.DELETED.getCode().equals(user.getDelFlag())
                || UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            return null;
        }
        if (CONTACT_EMAIL.equals(contactType) && StringUtils.isNotEmpty(user.getEmail())
                && user.getEmail().trim().equalsIgnoreCase(contact))
        {
            return user;
        }
        if (CONTACT_PHONE.equals(contactType) && StringUtils.isNotEmpty(user.getPhonenumber())
                && user.getPhonenumber().trim().equals(contact))
        {
            return user;
        }
        return null;
    }

    private void validateNewPassword(String password)
    {
        if (StringUtils.isEmpty(password))
        {
            throw new ServiceException("新密码不能为空");
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            throw new ServiceException("新密码长度必须在" + UserConstants.PASSWORD_MIN_LENGTH + "到"
                    + UserConstants.PASSWORD_MAX_LENGTH + "个字符之间");
        }
        String chrtype = passwordCharType();
        boolean valid;
        if ("1".equals(chrtype))
        {
            valid = password.matches("^[0-9]+$");
        }
        else if ("2".equals(chrtype))
        {
            valid = password.matches("^[a-zA-Z]+$");
        }
        else if ("3".equals(chrtype))
        {
            valid = password.matches("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$");
        }
        else if ("4".equals(chrtype))
        {
            valid = password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()\\-=_+])[A-Za-z\\d~!@#$%^&*()\\-=_+]+$");
        }
        else
        {
            valid = password.matches("^[^<>\"'|\\\\]+$");
        }
        if (!valid)
        {
            throw new ServiceException("新密码不符合密码规则");
        }
    }

    private MailConfig loadMailConfig()
    {
        String host = configValue(CONFIG_MAIL_HOST);
        int port = Math.max(1, intConfig(CONFIG_MAIL_PORT, 465));
        String username = configValue(CONFIG_MAIL_USERNAME);
        String password = configValue(CONFIG_MAIL_PASSWORD);
        String from = configValue(CONFIG_MAIL_FROM);
        boolean auth = booleanConfig(CONFIG_MAIL_AUTH, true);
        boolean sslEnable = booleanConfig(CONFIG_MAIL_SSL_ENABLE, true);
        boolean starttlsEnable = booleanConfig(CONFIG_MAIL_STARTTLS_ENABLE, false);
        int timeout = Math.max(1000, intConfig(CONFIG_MAIL_TIMEOUT, 10000));

        if (StringUtils.isEmpty(host))
        {
            throw new ServiceException("邮件服务未配置，请联系管理员");
        }
        if (auth && (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)))
        {
            throw new ServiceException("邮件服务未配置，请联系管理员");
        }
        if (StringUtils.isEmpty(from))
        {
            from = username;
        }
        if (StringUtils.isEmpty(from))
        {
            throw new ServiceException("邮件服务未配置，请联系管理员");
        }
        return new MailConfig(host, port, username, password, from, auth, sslEnable, starttlsEnable, timeout);
    }

    private void sendEmailCode(SysUser user, String email, String code, MailConfig mailConfig)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(mailConfig.from);
        message.setSubject("登录密码重置验证码");
        message.setText("您好，" + StringUtils.nvl(user.getNickName(), user.getUserName()) + "：\n\n"
                + "您的登录密码重置验证码为：" + code + "\n"
                + "验证码" + codeExpirationMinutes() + "分钟内有效。若非本人操作，请忽略本邮件。");
        try
        {
            createMailSender(mailConfig).send(message);
        }
        catch (MailException e)
        {
            throw new ServiceException("验证码邮件发送失败，请检查邮件服务配置或稍后重试");
        }
    }

    private JavaMailSenderImpl createMailSender(MailConfig config)
    {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(config.host);
        sender.setPort(config.port);
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());
        if (StringUtils.isNotEmpty(config.username))
        {
            sender.setUsername(config.username);
        }
        if (StringUtils.isNotEmpty(config.password))
        {
            sender.setPassword(config.password);
        }

        Properties properties = sender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", String.valueOf(config.auth));
        properties.put("mail.smtp.ssl.enable", String.valueOf(config.sslEnable));
        properties.put("mail.smtp.starttls.enable", String.valueOf(config.starttlsEnable));
        properties.put("mail.smtp.connectiontimeout", String.valueOf(config.timeout));
        properties.put("mail.smtp.timeout", String.valueOf(config.timeout));
        properties.put("mail.smtp.writetimeout", String.valueOf(config.timeout));
        return sender;
    }

    private String createVerifyCode()
    {
        return String.format("%06d", RANDOM.nextInt(1000000));
    }

    private String identityKey(String username, String contactType, String contact)
    {
        return sha256(trim(username).toLowerCase(Locale.ROOT) + ":" + contactType + ":" + trim(contact).toLowerCase(Locale.ROOT));
    }

    private String sha256(String value)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes)
            {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new ServiceException("验证码服务异常，请联系管理员");
        }
    }

    private String normalizeContactType(String contactType)
    {
        return trim(contactType).toLowerCase(Locale.ROOT);
    }

    private String trim(String value)
    {
        return value == null ? "" : value.trim();
    }

    private String configValue(String configKey)
    {
        return trim(configService.selectConfigByKey(configKey));
    }

    private int intConfig(String configKey, int defaultValue)
    {
        return Convert.toInt(configValue(configKey), defaultValue);
    }

    private boolean booleanConfig(String configKey, boolean defaultValue)
    {
        return Convert.toBool(configValue(configKey), defaultValue);
    }

    private boolean emailResetEnabled()
    {
        return Boolean.parseBoolean(Convert.toStr(configService.selectConfigByKey("sys.password.reset.emailEnabled"), "true"));
    }

    private boolean smsResetEnabled()
    {
        return Boolean.parseBoolean(Convert.toStr(configService.selectConfigByKey("sys.password.reset.smsEnabled"), "false"));
    }

    private int codeExpirationMinutes()
    {
        return Math.max(1, Convert.toInt(configService.selectConfigByKey("sys.password.reset.codeExpireMinutes"), 10));
    }

    private int resendIntervalSeconds()
    {
        return Math.max(1, Convert.toInt(configService.selectConfigByKey("sys.password.reset.resendSeconds"), 60));
    }

    private int maxErrorCount()
    {
        return Math.max(1, Convert.toInt(configService.selectConfigByKey("sys.password.reset.maxErrorCount"), 5));
    }

    private String passwordCharType()
    {
        return Convert.toStr(configService.selectConfigByKey("sys.account.chrtype"), "0");
    }

    private static class MailConfig
    {
        private final String host;

        private final int port;

        private final String username;

        private final String password;

        private final String from;

        private final boolean auth;

        private final boolean sslEnable;

        private final boolean starttlsEnable;

        private final int timeout;

        MailConfig(String host, int port, String username, String password, String from, boolean auth, boolean sslEnable,
                boolean starttlsEnable, int timeout)
        {
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
            this.from = from;
            this.auth = auth;
            this.sslEnable = sslEnable;
            this.starttlsEnable = starttlsEnable;
            this.timeout = timeout;
        }
    }
}
