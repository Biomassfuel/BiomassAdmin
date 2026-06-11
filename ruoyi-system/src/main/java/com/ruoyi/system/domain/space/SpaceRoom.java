package com.ruoyi.system.domain.space;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class SpaceRoom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long roomId;

    @Excel(name = "房间编号", prompt = "必填，作为唯一标识，如101、301")
    private String roomCode;

    @Excel(name = "房间名称", prompt = "可为空，为空时默认使用房间编号")
    private String roomName;

    private Long buildingId;

    @Excel(name = "楼栋名称")
    private String buildingName;

    @Excel(name = "楼层")
    private String floorNo;

    private Long typeId;

    @Excel(name = "房间类型")
    private String roomType;

    @Excel(name = "面积")
    private BigDecimal area;

    @Excel(name = "最小容纳人数")
    private Integer capacityMin;

    @Excel(name = "最大容纳人数")
    private Integer capacityMax;

    @Excel(name = "容量说明")
    private String capacityDesc;

    private Long assignedOrgId;

    @Excel(name = "归属单位")
    private String assignedOrgName;

    @Excel(name = "设备说明")
    private String equipmentDesc;

    private List<SpaceRoomEquipment> roomEquipmentList;

    @Excel(name = "位置说明")
    private String locationDesc;

    @Excel(name = "是否可预约", readConverterExp = "0=可预约,1=不可预约", combo = {"可预约", "不可预约"})
    private String bookable;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用", combo = {"正常", "停用"})
    private String status;

    private String delFlag;

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

    public Long getBuildingId()
    {
        return buildingId;
    }

    public void setBuildingId(Long buildingId)
    {
        this.buildingId = buildingId;
    }

    public String getBuildingName()
    {
        return buildingName;
    }

    public void setBuildingName(String buildingName)
    {
        this.buildingName = buildingName;
    }

    public String getFloorNo()
    {
        return floorNo;
    }

    public void setFloorNo(String floorNo)
    {
        this.floorNo = floorNo;
    }

    public Long getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Long typeId)
    {
        this.typeId = typeId;
    }

    public String getRoomType()
    {
        return roomType;
    }

    public void setRoomType(String roomType)
    {
        this.roomType = roomType;
    }

    public BigDecimal getArea()
    {
        return area;
    }

    public void setArea(BigDecimal area)
    {
        this.area = area;
    }

    public Integer getCapacityMin()
    {
        return capacityMin;
    }

    public void setCapacityMin(Integer capacityMin)
    {
        this.capacityMin = capacityMin;
    }

    public Integer getCapacityMax()
    {
        return capacityMax;
    }

    public void setCapacityMax(Integer capacityMax)
    {
        this.capacityMax = capacityMax;
    }

    public String getCapacityDesc()
    {
        return capacityDesc;
    }

    public void setCapacityDesc(String capacityDesc)
    {
        this.capacityDesc = capacityDesc;
    }

    public Long getAssignedOrgId()
    {
        return assignedOrgId;
    }

    public void setAssignedOrgId(Long assignedOrgId)
    {
        this.assignedOrgId = assignedOrgId;
    }

    public String getAssignedOrgName()
    {
        return assignedOrgName;
    }

    public void setAssignedOrgName(String assignedOrgName)
    {
        this.assignedOrgName = assignedOrgName;
    }

    public String getEquipmentDesc()
    {
        return equipmentDesc;
    }

    public void setEquipmentDesc(String equipmentDesc)
    {
        this.equipmentDesc = equipmentDesc;
    }

    public List<SpaceRoomEquipment> getRoomEquipmentList()
    {
        return roomEquipmentList;
    }

    public void setRoomEquipmentList(List<SpaceRoomEquipment> roomEquipmentList)
    {
        this.roomEquipmentList = roomEquipmentList;
    }

    public String getLocationDesc()
    {
        return locationDesc;
    }

    public void setLocationDesc(String locationDesc)
    {
        this.locationDesc = locationDesc;
    }

    public String getBookable()
    {
        return bookable;
    }

    public void setBookable(String bookable)
    {
        this.bookable = bookable;
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
