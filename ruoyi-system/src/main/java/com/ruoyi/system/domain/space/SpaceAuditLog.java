package com.ruoyi.system.domain.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.annotation.Excel;
import java.util.Date;

public class SpaceAuditLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long logId;
    @Excel(name = "预约ID")
    private Long reservationId;
    @Excel(name = "场次ID")
    private Long itemId;
    @Excel(name = "审核动作", readConverterExp = "0=提交,1=通过,2=驳回,4=取消,5=场次通过,6=场次驳回")
    private String auditAction;
    @Excel(name = "前状态")
    private String beforeStatus;
    @Excel(name = "后状态")
    private String afterStatus;
    private Long auditorId;
    @Excel(name = "操作人")
    private String auditorName;
    @Excel(name = "审核意见")
    private String auditOpinion;
    @Excel(name = "审核时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public Long getReservationId()
    {
        return reservationId;
    }

    public void setReservationId(Long reservationId)
    {
        this.reservationId = reservationId;
    }

    public Long getItemId()
    {
        return itemId;
    }

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public String getAuditAction()
    {
        return auditAction;
    }

    public void setAuditAction(String auditAction)
    {
        this.auditAction = auditAction;
    }

    public String getBeforeStatus()
    {
        return beforeStatus;
    }

    public void setBeforeStatus(String beforeStatus)
    {
        this.beforeStatus = beforeStatus;
    }

    public String getAfterStatus()
    {
        return afterStatus;
    }

    public void setAfterStatus(String afterStatus)
    {
        this.afterStatus = afterStatus;
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

    public String getAuditOpinion()
    {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion)
    {
        this.auditOpinion = auditOpinion;
    }

    public Date getAuditTime()
    {
        return auditTime;
    }

    public void setAuditTime(Date auditTime)
    {
        this.auditTime = auditTime;
    }

}
