package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceEquipment;

public interface SpaceEquipmentMapper
{
    public SpaceEquipment selectSpaceEquipmentById(Long equipmentId);
    public List<SpaceEquipment> selectSpaceEquipmentList(SpaceEquipment spaceEquipment);
    public int insertSpaceEquipment(SpaceEquipment spaceEquipment);
    public int updateSpaceEquipment(SpaceEquipment spaceEquipment);
    public int deleteSpaceEquipmentById(Long equipmentId);
    public int deleteSpaceEquipmentByIds(Long[] equipmentIds);
}