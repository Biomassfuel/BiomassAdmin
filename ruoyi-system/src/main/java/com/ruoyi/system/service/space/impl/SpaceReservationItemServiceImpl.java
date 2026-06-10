package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.space.SpaceReservationItem;
import com.ruoyi.system.mapper.space.SpaceReservationItemMapper;
import com.ruoyi.system.service.space.ISpaceReservationItemService;

@Service
public class SpaceReservationItemServiceImpl implements ISpaceReservationItemService
{
    @Autowired
    private SpaceReservationItemMapper spaceReservationItemMapper;

    @Override
    public SpaceReservationItem selectSpaceReservationItemById(Long itemId)
    {
        return spaceReservationItemMapper.selectSpaceReservationItemById(itemId);
    }

    @Override
    public List<SpaceReservationItem> selectSpaceReservationItemList(SpaceReservationItem spaceReservationItem)
    {
        return spaceReservationItemMapper.selectSpaceReservationItemList(spaceReservationItem);
    }

    @Override
    public int insertSpaceReservationItem(SpaceReservationItem spaceReservationItem)
    {
        return spaceReservationItemMapper.insertSpaceReservationItem(spaceReservationItem);
    }

    @Override
    public int updateSpaceReservationItem(SpaceReservationItem spaceReservationItem)
    {
        return spaceReservationItemMapper.updateSpaceReservationItem(spaceReservationItem);
    }

    @Override
    public int deleteSpaceReservationItemByIds(Long[] itemIds)
    {
        return spaceReservationItemMapper.deleteSpaceReservationItemByIds(itemIds);
    }

    @Override
    public int deleteSpaceReservationItemById(Long itemId)
    {
        return spaceReservationItemMapper.deleteSpaceReservationItemById(itemId);
    }
}