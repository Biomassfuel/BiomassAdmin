package com.ruoyi.system.domain.space;

import com.ruoyi.common.core.domain.BaseEntity;

public class SpaceRoomEquipment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long roomEquipmentId;
    private Long roomId;
    private Long equipmentId;

    private String equipmentCode;

    private String equipmentName;
    private Integer quantity;
    private String status;

    public Long getRoomEquipmentId()
    {
        return roomEquipmentId;
    }

    public void setRoomEquipmentId(Long roomEquipmentId)
    {
        this.roomEquipmentId = roomEquipmentId;
    }

    public Long getRoomId()
    {
        return roomId;
    }

    public void setRoomId(Long roomId)
    {
        this.roomId = roomId;
    }

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



    public Integer getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
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
