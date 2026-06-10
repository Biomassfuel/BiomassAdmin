package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.space.SpaceBuilding;
import com.ruoyi.system.mapper.space.SpaceBuildingMapper;
import com.ruoyi.system.service.space.ISpaceBuildingService;

@Service
public class SpaceBuildingServiceImpl implements ISpaceBuildingService
{
    @Autowired
    private SpaceBuildingMapper spaceBuildingMapper;

    @Override
    public SpaceBuilding selectSpaceBuildingById(Long buildingId)
    {
        return spaceBuildingMapper.selectSpaceBuildingById(buildingId);
    }

    @Override
    public List<SpaceBuilding> selectSpaceBuildingList(SpaceBuilding spaceBuilding)
    {
        return spaceBuildingMapper.selectSpaceBuildingList(spaceBuilding);
    }

    @Override
    public int insertSpaceBuilding(SpaceBuilding spaceBuilding)
    {
        return spaceBuildingMapper.insertSpaceBuilding(spaceBuilding);
    }

    @Override
    public int updateSpaceBuilding(SpaceBuilding spaceBuilding)
    {
        return spaceBuildingMapper.updateSpaceBuilding(spaceBuilding);
    }

    @Override
    public int deleteSpaceBuildingByIds(Long[] buildingIds)
    {
        return spaceBuildingMapper.deleteSpaceBuildingByIds(buildingIds);
    }

    @Override
    public int deleteSpaceBuildingById(Long buildingId)
    {
        return spaceBuildingMapper.deleteSpaceBuildingById(buildingId);
    }
}