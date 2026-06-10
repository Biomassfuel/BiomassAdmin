package com.ruoyi.system.domain.space;

import com.ruoyi.common.core.domain.BaseEntity;

public class SpaceReservationRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long ruleId;
    private Long reservationId;
    private String ruleType;
    private Long roomId;
    private String roomCode;
    private String startDate;
    private String endDate;
    private String weekdays;
    private String customDatesText;
    private String startTime;
    private String endTime;
    private String ruleDesc;

    public Long getRuleId()
    {
        return ruleId;
    }

    public void setRuleId(Long ruleId)
    {
        this.ruleId = ruleId;
    }

    public Long getReservationId()
    {
        return reservationId;
    }

    public void setReservationId(Long reservationId)
    {
        this.reservationId = reservationId;
    }

    public String getRuleType()
    {
        return ruleType;
    }

    public void setRuleType(String ruleType)
    {
        this.ruleType = ruleType;
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

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getWeekdays()
    {
        return weekdays;
    }

    public void setWeekdays(String weekdays)
    {
        this.weekdays = weekdays;
    }

    public String getCustomDatesText()
    {
        return customDatesText;
    }

    public void setCustomDatesText(String customDatesText)
    {
        this.customDatesText = customDatesText;
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

    public String getRuleDesc()
    {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc)
    {
        this.ruleDesc = ruleDesc;
    }

}