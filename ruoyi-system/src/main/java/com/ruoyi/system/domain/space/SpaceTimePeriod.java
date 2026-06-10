package com.ruoyi.system.domain.space;

import com.ruoyi.common.core.domain.BaseEntity;

public class SpaceTimePeriod extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long periodId;
    private String periodCode;
    private String periodName;
    private String startTime;
    private String endTime;
    private Integer orderNum;
    private String status;

    public Long getPeriodId()
    {
        return periodId;
    }

    public void setPeriodId(Long periodId)
    {
        this.periodId = periodId;
    }

    public String getPeriodCode()
    {
        return periodCode;
    }

    public void setPeriodCode(String periodCode)
    {
        this.periodCode = periodCode;
    }

    public String getPeriodName()
    {
        return periodName;
    }

    public void setPeriodName(String periodName)
    {
        this.periodName = periodName;
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

    public Integer getOrderNum()
    {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum)
    {
        this.orderNum = orderNum;
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