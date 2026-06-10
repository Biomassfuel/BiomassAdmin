package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceEquipment;

public interface ISpaceEquipmentService
{
    public SpaceEquipment selectSpaceEquipmentById(Long equipmentId);
    public List<SpaceEquipment> selectSpaceEquipmentList(SpaceEquipment spaceEquipment);
    public int insertSpaceEquipment(SpaceEquipment spaceEquipment);
    public int updateSpaceEquipment(SpaceEquipment spaceEquipment);
    public int deleteSpaceEquipmentByIds(Long[] equipmentIds);
    public int deleteSpaceEquipmentById(Long equipmentId);
}