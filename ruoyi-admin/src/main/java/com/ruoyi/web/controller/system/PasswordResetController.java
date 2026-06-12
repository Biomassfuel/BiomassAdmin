package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.PasswordResetBody;
import com.ruoyi.common.core.domain.model.PasswordResetCodeBody;
import com.ruoyi.framework.web.service.PasswordResetService;

/**
 * Public password reset endpoints.
 */
@RestController
@RequestMapping("/password/reset")
public class PasswordResetController
{
    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/config")
    public AjaxResult config()
    {
        return AjaxResult.success(passwordResetService.getResetConfig());
    }

    @PostMapping("/code")
    public AjaxResult sendCode(@RequestBody PasswordResetCodeBody body)
    {
        return AjaxResult.success(passwordResetService.sendResetCode(body));
    }

    @PostMapping
    public AjaxResult reset(@RequestBody PasswordResetBody body)
    {
        passwordResetService.resetPassword(body);
        return AjaxResult.success("密码重置成功，请使用新密码登录");
    }
}
