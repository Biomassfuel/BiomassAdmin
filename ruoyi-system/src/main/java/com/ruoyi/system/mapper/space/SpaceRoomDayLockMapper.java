package com.ruoyi.system.mapper.space;

import org.apache.ibatis.annotations.Param;

public interface SpaceRoomDayLockMapper
{
    public int insertIgnore(@Param("roomId") Long roomId, @Param("bookingDate") String bookingDate, @Param("createBy") String createBy);

    public Integer lockRoomDay(@Param("roomId") Long roomId, @Param("bookingDate") String bookingDate);
}
