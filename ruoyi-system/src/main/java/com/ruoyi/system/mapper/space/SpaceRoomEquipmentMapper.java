package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceRoomEquipment;

public interface SpaceRoomEquipmentMapper
{
    public SpaceRoomEquipment selectSpaceRoomEquipmentById(Long roomEquipmentId);
    public List<SpaceRoomEquipment> selectSpaceRoomEquipmentList(SpaceRoomEquipment spaceRoomEquipment);
    public int insertSpaceRoomEquipment(SpaceRoomEquipment spaceRoomEquipment);

    public int batchInsertSpaceRoomEquipment(@Param("list") List<SpaceRoomEquipment> spaceRoomEquipmentList);
    public int updateSpaceRoomEquipment(SpaceRoomEquipment spaceRoomEquipment);
    public int deleteSpaceRoomEquipmentById(Long roomEquipmentId);
    public int deleteSpaceRoomEquipmentByIds(Long[] roomEquipmentIds);

    public int deleteSpaceRoomEquipmentByRoomId(Long roomId);

    public int deleteSpaceRoomEquipmentByRoomIds(Long[] roomIds);
}
