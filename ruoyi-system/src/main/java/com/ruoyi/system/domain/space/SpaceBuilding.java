package com.ruoyi.system.domain.space;

import com.ruoyi.common.core.domain.BaseEntity;

public class SpaceBuilding extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long buildingId;
    private String buildingCode;
    private String buildingName;
    private String campusName;
    private String address;
    private Integer floorCount;
    private String status;
    private String delFlag;

    public Long getBuildingId()
    {
        return buildingId;
    }

    public void setBuildingId(Long buildingId)
    {
        this.buildingId = buildingId;
    }

    public String getBuildingCode()
    {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode)
    {
        this.buildingCode = buildingCode;
    }

    public String getBuildingName()
    {
        return buildingName;
    }

    public void setBuildingName(String buildingName)
    {
        this.buildingName = buildingName;
    }

    public String getCampusName()
    {
        return campusName;
    }

    public void setCampusName(String campusName)
    {
        this.campusName = campusName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public Integer getFloorCount()
    {
        return floorCount;
    }

    public void setFloorCount(Integer floorCount)
    {
        this.floorCount = floorCount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

}