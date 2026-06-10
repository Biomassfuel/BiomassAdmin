package com.ruoyi.system.domain.space;

import com.ruoyi.common.core.domain.BaseEntity;

public class SpaceBlackout extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long blackoutId;
    private Long roomId;
    private String roomCode;
    private String startTime;
    private String endTime;
    private String reason;
    private String status;

    public Long getBlackoutId()
    {
        return blackoutId;
    }

    public void setBlackoutId(Long blackoutId)
    {
        this.blackoutId = blackoutId;
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

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

}