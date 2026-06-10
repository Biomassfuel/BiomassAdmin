package com.ruoyi.system.domain.space;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.annotation.Excel;

public class SpaceRoomType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long typeId;
    @Excel(name = "类型编码")
    private String typeCode;
    @Excel(name = "类型名称")
    private String typeName;
    @Excel(name = "容量等级")
    private String capacityLevel;
    @Excel(name = "排序")
    private Integer orderNum;
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public Long getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Long typeId)
    {
        this.typeId = typeId;
    }

    public String getTypeCode()
    {
        return typeCode;
    }

    public void setTypeCode(String typeCode)
    {
        this.typeCode = typeCode;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public String getCapacityLevel()
    {
        return capacityLevel;
    }

    public void setCapacityLevel(String capacityLevel)
    {
        this.capacityLevel = capacityLevel;
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
