package com.ruoyi.system.domain.space;

import com.ruoyi.common.core.domain.BaseEntity;

public class SpaceOrg extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long orgId;
    private String orgCode;
    private String orgName;
    private String orgShortName;
    private String orgType;
    private Long deptId;
    private String contactName;
    private String contactPhone;
    private String status;
    private String delFlag;

    public Long getOrgId()
    {
        return orgId;
    }

    public void setOrgId(Long orgId)
    {
        this.orgId = orgId;
    }

    public String getOrgCode()
    {
        return orgCode;
    }

    public void setOrgCode(String orgCode)
    {
        this.orgCode = orgCode;
    }

    public String getOrgName()
    {
        return orgName;
    }

    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }

    public String getOrgShortName()
    {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName)
    {
        this.orgShortName = orgShortName;
    }

    public String getOrgType()
    {
        return orgType;
    }

    public void setOrgType(String orgType)
    {
        this.orgType = orgType;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

}