package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.space.SpaceBlackout;
import com.ruoyi.system.mapper.space.SpaceBlackoutMapper;
import com.ruoyi.system.service.space.ISpaceBlackoutService;

@Service
public class SpaceBlackoutServiceImpl implements ISpaceBlackoutService
{
    @Autowired
    private SpaceBlackoutMapper spaceBlackoutMapper;

    @Override
    public SpaceBlackout selectSpaceBlackoutById(Long blackoutId)
    {
        return spaceBlackoutMapper.selectSpaceBlackoutById(blackoutId);
    }

    @Override
    public List<SpaceBlackout> selectSpaceBlackoutList(SpaceBlackout spaceBlackout)
    {
        return spaceBlackoutMapper.selectSpaceBlackoutList(spaceBlackout);
    }

    @Override
    public int insertSpaceBlackout(SpaceBlackout spaceBlackout)
    {
        return spaceBlackoutMapper.insertSpaceBlackout(spaceBlackout);
    }

    @Override
    public int updateSpaceBlackout(SpaceBlackout spaceBlackout)
    {
        return spaceBlackoutMapper.updateSpaceBlackout(spaceBlackout);
    }

    @Override
    public int deleteSpaceBlackoutByIds(Long[] blackoutIds)
    {
        return spaceBlackoutMapper.deleteSpaceBlackoutByIds(blackoutIds);
    }

    @Override
    public int deleteSpaceBlackoutById(Long blackoutId)
    {
        return spaceBlackoutMapper.deleteSpaceBlackoutById(blackoutId);
    }
}