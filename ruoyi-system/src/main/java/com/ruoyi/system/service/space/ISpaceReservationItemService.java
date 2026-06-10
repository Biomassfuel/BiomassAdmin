package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceReservationItem;

public interface ISpaceReservationItemService
{
    public SpaceReservationItem selectSpaceReservationItemById(Long itemId);
    public List<SpaceReservationItem> selectSpaceReservationItemList(SpaceReservationItem spaceReservationItem);
    public int insertSpaceReservationItem(SpaceReservationItem spaceReservationItem);
    public int updateSpaceReservationItem(SpaceReservationItem spaceReservationItem);
    public int deleteSpaceReservationItemByIds(Long[] itemIds);
    public int deleteSpaceReservationItemById(Long itemId);
}