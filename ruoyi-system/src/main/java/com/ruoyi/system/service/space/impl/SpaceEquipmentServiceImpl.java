package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.space.SpaceEquipment;
import com.ruoyi.system.mapper.space.SpaceEquipmentMapper;
import com.ruoyi.system.service.space.ISpaceEquipmentService;

@Service
public class SpaceEquipmentServiceImpl implements ISpaceEquipmentService
{
    @Autowired
    private SpaceEquipmentMapper spaceEquipmentMapper;

    @Override
    public SpaceEquipment selectSpaceEquipmentById(Long equipmentId)
    {
        return spaceEquipmentMapper.selectSpaceEquipmentById(equipmentId);
    }

    @Override
    public List<SpaceEquipment> selectSpaceEquipmentList(SpaceEquipment spaceEquipment)
    {
        return spaceEquipmentMapper.selectSpaceEquipmentList(spaceEquipment);
    }

    @Override
    public int insertSpaceEquipment(SpaceEquipment spaceEquipment)
    {
        return spaceEquipmentMapper.insertSpaceEquipment(spaceEquipment);
    }

    @Override
    public int updateSpaceEquipment(SpaceEquipment spaceEquipment)
    {
        return spaceEquipmentMapper.updateSpaceEquipment(spaceEquipment);
    }

    @Override
    public int deleteSpaceEquipmentByIds(Long[] equipmentIds)
    {
        return spaceEquipmentMapper.deleteSpaceEquipmentByIds(equipmentIds);
    }

    @Override
    public int deleteSpaceEquipmentById(Long equipmentId)
    {
        return spaceEquipmentMapper.deleteSpaceEquipmentById(equipmentId);
    }
}