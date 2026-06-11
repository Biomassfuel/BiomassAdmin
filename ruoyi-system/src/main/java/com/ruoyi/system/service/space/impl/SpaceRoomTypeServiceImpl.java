package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;

import com.ruoyi.common.utils.StringUtils;

import com.ruoyi.system.domain.space.SpaceRoomType;
import com.ruoyi.system.mapper.space.SpaceRoomTypeMapper;
import com.ruoyi.system.service.space.ISpaceRoomTypeService;

@Service
public class SpaceRoomTypeServiceImpl implements ISpaceRoomTypeService
{
    @Autowired
    private SpaceRoomTypeMapper spaceRoomTypeMapper;

    @Override
    public SpaceRoomType selectSpaceRoomTypeById(Long typeId)
    {
        return spaceRoomTypeMapper.selectSpaceRoomTypeById(typeId);
    }

    @Override
    public List<SpaceRoomType> selectSpaceRoomTypeList(SpaceRoomType spaceRoomType)
    {
        return spaceRoomTypeMapper.selectSpaceRoomTypeList(spaceRoomType);
    }

    @Override
    public int insertSpaceRoomType(SpaceRoomType spaceRoomType)
    {
        validateTypeCodeUnique(spaceRoomType);

        return spaceRoomTypeMapper.insertSpaceRoomType(spaceRoomType);
    }

    @Override
    public int updateSpaceRoomType(SpaceRoomType spaceRoomType)
    {
        validateTypeCodeUnique(spaceRoomType);

        return spaceRoomTypeMapper.updateSpaceRoomType(spaceRoomType);
    }

    @Override
    public int deleteSpaceRoomTypeByIds(Long[] typeIds)
    {
        return spaceRoomTypeMapper.deleteSpaceRoomTypeByIds(typeIds);
    }

    @Override
    public int deleteSpaceRoomTypeById(Long typeId)
    {
        return spaceRoomTypeMapper.deleteSpaceRoomTypeById(typeId);
    }
    private void validateTypeCodeUnique(SpaceRoomType spaceRoomType)

    {

        if (StringUtils.isBlank(spaceRoomType.getTypeCode()))

        {

            throw new ServiceException("类型编码不能为空");

        }

        SpaceRoomType existing = spaceRoomTypeMapper.selectSpaceRoomTypeByCode(spaceRoomType.getTypeCode());

        if (existing != null && (spaceRoomType.getTypeId() == null || !existing.getTypeId().equals(spaceRoomType.getTypeId())))

        {

            throw new ServiceException("类型编码“" + spaceRoomType.getTypeCode() + "”已存在");

        }

    }

}
