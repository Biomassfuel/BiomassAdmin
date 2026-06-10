package com.ruoyi.system.domain.space;

import java.math.BigDecimal;

import com.ruoyi.common.annotation.Excel;

public class SpaceRoomStatistics
{
    @Excel(name = "房间编号")
    private String roomCode;

    @Excel(name = "房间名称")
    private String roomName;

    @Excel(name = "预约频次")
    private Long reservationCount;

    @Excel(name = "占用率")
    private BigDecimal occupancyRate;

    @Excel(name = "预约成功数量")
    private Long successCount;

    @Excel(name = "预约驳回数量")
    private Long rejectCount;

    @Excel(name = "预约总时长")
    private BigDecimal occupiedHours;

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

    public Long getReservationCount()
    {
        return reservationCount;
    }

    public void setReservationCount(Long reservationCount)
    {
        this.reservationCount = reservationCount;
    }

    public BigDecimal getOccupancyRate()
    {
        return occupancyRate;
    }

    public void setOccupancyRate(BigDecimal occupancyRate)
    {
        this.occupancyRate = occupancyRate;
    }

    public Long getSuccessCount()
    {
        return successCount;
    }

    public void setSuccessCount(Long successCount)
    {
        this.successCount = successCount;
    }

    public Long getRejectCount()
    {
        return rejectCount;
    }

    public void setRejectCount(Long rejectCount)
    {
        this.rejectCount = rejectCount;
    }

    public BigDecimal getOccupiedHours()
    {
        return occupiedHours;
    }

    public void setOccupiedHours(BigDecimal occupiedHours)
    {
        this.occupiedHours = occupiedHours;
    }
}
