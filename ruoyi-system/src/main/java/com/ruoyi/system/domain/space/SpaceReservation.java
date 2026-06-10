package com.ruoyi.system.domain.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.annotation.Excel;
import java.util.Date;
import java.util.List;

public class SpaceReservation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long reservationId;
    @Excel(name = "预约编号")
    private String reservationNo;
    @Excel(name = "预约类型", readConverterExp = "0=普通预约,1=长期预约")
    private String reservationType;
    private Long applicantId;
    @Excel(name = "申请人")
    private String applicantName;
    @Excel(name = "申请角色")
    private String applicantRole;
    @Excel(name = "联系电话")
    private String applicantPhone;
    private Long orgId;
    @Excel(name = "申请单位")
    private String orgName;
    @Excel(name = "预约主题")
    private String title;
    @Excel(name = "预约用途")
    private String purpose;
    @Excel(name = "预约人数")
    private Integer peopleCount;
    @Excel(name = "备注")
    private String detailRemark;
    @Excel(name = "状态", readConverterExp = "0=草稿,1=待审核,2=已通过,3=部分通过,4=已驳回,5=已取消,6=已结束")
    private String status;
    @Excel(name = "提交时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;
    private Long auditorId;
    @Excel(name = "审核人")
    private String auditorName;
    @Excel(name = "审核时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    @Excel(name = "驳回原因")
    private String rejectReason;
    private Integer version;
    private String delFlag;
    private Boolean pendingOnly;

    private List<SpaceReservationItem> items;
    private SpaceReservationRule rule;

    public Long getReservationId()
    {
        return reservationId;
    }

    public void setReservationId(Long reservationId)
    {
        this.reservationId = reservationId;
    }

    public String getReservationNo()
    {
        return reservationNo;
    }

    public void setReservationNo(String reservationNo)
    {
        this.reservationNo = reservationNo;
    }

    public String getReservationType()
    {
        return reservationType;
    }

    public void setReservationType(String reservationType)
    {
        this.reservationType = reservationType;
    }

    public Long getApplicantId()
    {
        return applicantId;
    }

    public void setApplicantId(Long applicantId)
    {
        this.applicantId = applicantId;
    }

    public String getApplicantName()
    {
        return applicantName;
    }

    public void setApplicantName(String applicantName)
    {
        this.applicantName = applicantName;
    }

    public String getApplicantRole()
    {
        return applicantRole;
    }

    public void setApplicantRole(String applicantRole)
    {
        this.applicantRole = applicantRole;
    }

    public String getApplicantPhone()
    {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone)
    {
        this.applicantPhone = applicantPhone;
    }

    public Long getOrgId()
    {
        return orgId;
    }

    public void setOrgId(Long orgId)
    {
        this.orgId = orgId;
    }

    public String getOrgName()
    {
        return orgName;
    }

    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getPurpose()
    {
        return purpose;
    }

    public void setPurpose(String purpose)
    {
        this.purpose = purpose;
    }

    public Integer getPeopleCount()
    {
        return peopleCount;
    }

    public void setPeopleCount(Integer peopleCount)
    {
        this.peopleCount = peopleCount;
    }

    public String getDetailRemark()
    {
        return detailRemark;
    }

    public void setDetailRemark(String detailRemark)
    {
        this.detailRemark = detailRemark;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getSubmitTime()
    {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime)
    {
        this.submitTime = submitTime;
    }

    public Long getAuditorId()
    {
        return auditorId;
    }

    public void setAuditorId(Long auditorId)
    {
        this.auditorId = auditorId;
    }

    public String getAuditorName()
    {
        return auditorName;
    }

    public void setAuditorName(String auditorName)
    {
        this.auditorName = auditorName;
    }

    public Date getAuditTime()
    {
        return auditTime;
    }

    public void setAuditTime(Date auditTime)
    {
        this.auditTime = auditTime;
    }

    public String getRejectReason()
    {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason)
    {
        this.rejectReason = rejectReason;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public Boolean getPendingOnly()

    {

        return pendingOnly;

    }



    public void setPendingOnly(Boolean pendingOnly)

    {

        this.pendingOnly = pendingOnly;

    }



    public List<SpaceReservationItem> getItems()
    {
        return items;
    }

    public void setItems(List<SpaceReservationItem> items)
    {
        this.items = items;
    }

    public SpaceReservationRule getRule()
    {
        return rule;
    }

    public void setRule(SpaceReservationRule rule)
    {
        this.rule = rule;
    }

}
