package com.ruoyi.system.domain.space;

public class SpaceStatisticsTrend
{
    private String statDate;
    private Long reservationCount;
    private Long successCount;
    private Long rejectCount;

    public String getStatDate()
    {
        return statDate;
    }

    public void setStatDate(String statDate)
    {
        this.statDate = statDate;
    }

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
}
