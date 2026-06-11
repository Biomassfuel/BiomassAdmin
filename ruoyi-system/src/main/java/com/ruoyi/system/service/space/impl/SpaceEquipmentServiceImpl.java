package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;

import com.ruoyi.common.utils.StringUtils;

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
        validateEquipmentCodeUnique(spaceEquipment);

        return spaceEquipmentMapper.insertSpaceEquipment(spaceEquipment);
    }

    @Override
    public int updateSpaceEquipment(SpaceEquipment spaceEquipment)
    {
        validateEquipmentCodeUnique(spaceEquipment);

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
    private void validateEquipmentCodeUnique(SpaceEquipment spaceEquipment)

    {

        if (StringUtils.isBlank(spaceEquipment.getEquipmentCode()))

        {

            throw new ServiceException("设备编码不能为空");

        }

        SpaceEquipment existing = spaceEquipmentMapper.selectSpaceEquipmentByCode(spaceEquipment.getEquipmentCode());

        if (existing != null && (spaceEquipment.getEquipmentId() == null || !existing.getEquipmentId().equals(spaceEquipment.getEquipmentId())))

        {

            throw new ServiceException("设备编码“" + spaceEquipment.getEquipmentCode() + "”已存在");

        }

    }

}
