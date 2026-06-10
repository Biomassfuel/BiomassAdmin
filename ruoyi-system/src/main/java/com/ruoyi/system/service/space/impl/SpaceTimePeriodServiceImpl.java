package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.space.SpaceTimePeriod;
import com.ruoyi.system.mapper.space.SpaceTimePeriodMapper;
import com.ruoyi.system.service.space.ISpaceTimePeriodService;

@Service
public class SpaceTimePeriodServiceImpl implements ISpaceTimePeriodService
{
    @Autowired
    private SpaceTimePeriodMapper spaceTimePeriodMapper;

    @Override
    public SpaceTimePeriod selectSpaceTimePeriodById(Long periodId)
    {
        return spaceTimePeriodMapper.selectSpaceTimePeriodById(periodId);
    }

    @Override
    public List<SpaceTimePeriod> selectSpaceTimePeriodList(SpaceTimePeriod spaceTimePeriod)
    {
        return spaceTimePeriodMapper.selectSpaceTimePeriodList(spaceTimePeriod);
    }

    @Override
    public int insertSpaceTimePeriod(SpaceTimePeriod spaceTimePeriod)
    {
        return spaceTimePeriodMapper.insertSpaceTimePeriod(spaceTimePeriod);
    }

    @Override
    public int updateSpaceTimePeriod(SpaceTimePeriod spaceTimePeriod)
    {
        return spaceTimePeriodMapper.updateSpaceTimePeriod(spaceTimePeriod);
    }

    @Override
    public int deleteSpaceTimePeriodByIds(Long[] periodIds)
    {
        return spaceTimePeriodMapper.deleteSpaceTimePeriodByIds(periodIds);
    }

    @Override
    public int deleteSpaceTimePeriodById(Long periodId)
    {
        return spaceTimePeriodMapper.deleteSpaceTimePeriodById(periodId);
    }
}