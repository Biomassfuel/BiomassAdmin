package com.ruoyi.common.core.domain.model;

/**
 * Password reset verification-code request.
 */
public class PasswordResetCodeBody
{
    private String username;

    private String contactType;

    private String contact;

    private String code;

    private String uuid;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getContactType()
    {
        return contactType;
    }

    public void setContactType(String contactType)
    {
        this.contactType = contactType;
    }

    public String getContact()
    {
        return contact;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }
}
