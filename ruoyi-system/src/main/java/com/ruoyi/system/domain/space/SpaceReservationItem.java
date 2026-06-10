package com.ruoyi.system.domain.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.annotation.Excel;
import java.util.Date;

public class SpaceReservationItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long itemId;
    private Long reservationId;
    private Long roomId;
    @Excel(name = "房间编号")
    private String roomCode;
    @Excel(name = "房间名称")
    private String roomName;
    @Excel(name = "预约日期")
    private String bookingDate;
    @Excel(name = "星期")
    private String weekday;
    @Excel(name = "开始时间")
    private String startTime;
    @Excel(name = "结束时间")
    private String endTime;
    @Excel(name = "场次状态", readConverterExp = "1=待审核,2=已通过,3=已驳回,4=冲突待处理,5=已取消,6=已结束")
    private String itemStatus;
    @Excel(name = "冲突标识", readConverterExp = "0=否,1=是")
    private String conflictFlag;
    @Excel(name = "冲突原因")
    private String conflictReason;
    private Long conflictItemId;
    @Excel(name = "审核时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    private Long auditorId;
    @Excel(name = "审核人")
    private String auditorName;
    @Excel(name = "驳回原因")
    private String rejectReason;

    public Long getItemId()
    {
        return itemId;
    }

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getReservationId()
    {
        return reservationId;
    }

    public void setReservationId(Long reservationId)
    {
        this.reservationId = reservationId;
    }

    public Long getRoomId()
    {
        return roomId;
    }

    public void setRoomId(Long roomId)
    {
        this.roomId = roomId;
    }

    public String getRoomCode()
    {
        return roomCode;
    }

    public void setRoomCode(String roomCode)
    {
        this.roomCode = roomCode;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }

    public String getBookingDate()
    {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate)
    {
        this.bookingDate = bookingDate;
    }

    public String getWeekday()
    {
        return weekday;
    }

    public void setWeekday(String weekday)
    {
        this.weekday = weekday;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getItemStatus()
    {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus)
    {
        this.itemStatus = itemStatus;
    }

    public String getConflictFlag()
    {
        return conflictFlag;
    }

    public void setConflictFlag(String conflictFlag)
    {
        this.conflictFlag = conflictFlag;
    }

    public String getConflictReason()
    {
        return conflictReason;
    }

    public void setConflictReason(String conflictReason)
    {
        this.conflictReason = conflictReason;
    }

    public Long getConflictItemId()
    {
        return conflictItemId;
    }

    public void setConflictItemId(Long conflictItemId)
    {
        this.conflictItemId = conflictItemId;
    }

    public Date getAuditTime()
    {
        return auditTime;
    }

    public void setAuditTime(Date auditTime)
    {
        this.auditTime = auditTime;
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

    public String getRejectReason()
    {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason)
    {
        this.rejectReason = rejectReason;
    }

}
