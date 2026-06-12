package com.ruoyi.system.domain.space;

public class SpaceStatisticsQuery
{
    private String startDate;
    private String endDate;
    private Long roomId;
    private String roomCode;
    private String roomName;

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
}
