package com.ruoyi.system.domain.space;

import java.math.BigDecimal;

public class SpaceStatisticsSummary
{
    private Long reservationCount;
    private Long successCount;
    private Long rejectCount;
    private Long pendingCount;
    private Long roomCount;
    private BigDecimal occupancyRate;

    public Long getReservationCount()
    {
        return reservationCount;
    }

    public void setReservationCount(Long reservationCount)
    {
        this.reservationCount = reservationCount;
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

    public Long getPendingCount()
    {
        return pendingCount;
    }

    public void setPendingCount(Long pendingCount)
    {
        this.pendingCount = pendingCount;
    }

    public Long getRoomCount()
    {
        return roomCount;
    }

    public void setRoomCount(Long roomCount)
    {
        this.roomCount = roomCount;
    }

    public BigDecimal getOccupancyRate()
    {
        return occupancyRate;
    }

    public void setOccupancyRate(BigDecimal occupancyRate)
    {
        this.occupancyRate = occupancyRate;
    }
}
