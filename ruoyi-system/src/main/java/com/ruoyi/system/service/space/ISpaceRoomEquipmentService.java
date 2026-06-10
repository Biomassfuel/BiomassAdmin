package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceRoomEquipment;

public interface ISpaceRoomEquipmentService
{
    public SpaceRoomEquipment selectSpaceRoomEquipmentById(Long roomEquipmentId);
    public List<SpaceRoomEquipment> selectSpaceRoomEquipmentList(SpaceRoomEquipment spaceRoomEquipment);
    public int insertSpaceRoomEquipment(SpaceRoomEquipment spaceRoomEquipment);
    public int updateSpaceRoomEquipment(SpaceRoomEquipment spaceRoomEquipment);
    public int deleteSpaceRoomEquipmentByIds(Long[] roomEquipmentIds);
    public int deleteSpaceRoomEquipmentById(Long roomEquipmentId);
}