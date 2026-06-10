package com.ruoyi.system.domain.space;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.annotation.Excel;

public class SpaceEquipment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long equipmentId;
    @Excel(name = "设备编码")
    private String equipmentCode;
    @Excel(name = "设备名称")
    private String equipmentName;
    @Excel(name = "排序")
    private Integer orderNum;
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public Long getEquipmentId()
    {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId)
    {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentCode()
    {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode)
    {
        this.equipmentCode = equipmentCode;
    }

    public String getEquipmentName()
    {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName)
    {
        this.equipmentName = equipmentName;
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
