package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceReservationItem;

public interface SpaceReservationItemMapper
{
    public SpaceReservationItem selectSpaceReservationItemById(Long itemId);

    public List<SpaceReservationItem> selectSpaceReservationItemList(SpaceReservationItem spaceReservationItem);

    public int insertSpaceReservationItem(SpaceReservationItem spaceReservationItem);

    public int updateSpaceReservationItem(SpaceReservationItem spaceReservationItem);

    public int deleteSpaceReservationItemById(Long itemId);

    public int deleteSpaceReservationItemByIds(Long[] itemIds);

    public List<SpaceReservationItem> selectConflictItems(SpaceReservationItem item);

    public List<SpaceReservationItem> selectBlockingRoomReservationItems(@Param("roomIds") Long[] roomIds);

    public int updateItemStatus(SpaceReservationItem item);

    public List<Long> selectReservationIdsToFinish();

    public int updateFinishedItems(@Param("reservationIds") List<Long> reservationIds);
}
