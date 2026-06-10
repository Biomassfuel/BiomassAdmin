package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.space.SpaceRoomEquipment;
import com.ruoyi.system.mapper.space.SpaceRoomEquipmentMapper;
import com.ruoyi.system.service.space.ISpaceRoomEquipmentService;

@Service
public class SpaceRoomEquipmentServiceImpl implements ISpaceRoomEquipmentService
{
    @Autowired
    private SpaceRoomEquipmentMapper spaceRoomEquipmentMapper;

    @Override
    public SpaceRoomEquipment selectSpaceRoomEquipmentById(Long roomEquipmentId)
    {
        return spaceRoomEquipmentMapper.selectSpaceRoomEquipmentById(roomEquipmentId);
    }

    @Override
    public List<SpaceRoomEquipment> selectSpaceRoomEquipmentList(SpaceRoomEquipment spaceRoomEquipment)
    {
        return spaceRoomEquipmentMapper.selectSpaceRoomEquipmentList(spaceRoomEquipment);
    }

    @Override
    public int insertSpaceRoomEquipment(SpaceRoomEquipment spaceRoomEquipment)
    {
        return spaceRoomEquipmentMapper.insertSpaceRoomEquipment(spaceRoomEquipment);
    }

    @Override
    public int updateSpaceRoomEquipment(SpaceRoomEquipment spaceRoomEquipment)
    {
        return spaceRoomEquipmentMapper.updateSpaceRoomEquipment(spaceRoomEquipment);
    }

    @Override
    public int deleteSpaceRoomEquipmentByIds(Long[] roomEquipmentIds)
    {
        return spaceRoomEquipmentMapper.deleteSpaceRoomEquipmentByIds(roomEquipmentIds);
    }

    @Override
    public int deleteSpaceRoomEquipmentById(Long roomEquipmentId)
    {
        return spaceRoomEquipmentMapper.deleteSpaceRoomEquipmentById(roomEquipmentId);
    }
}