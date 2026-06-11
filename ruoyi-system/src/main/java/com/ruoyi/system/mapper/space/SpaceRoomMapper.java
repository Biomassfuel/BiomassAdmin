package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceRoom;

public interface SpaceRoomMapper
{
    public SpaceRoom selectSpaceRoomById(Long roomId);

    public SpaceRoom selectSpaceRoomByCode(@Param("roomCode") String roomCode);

    public SpaceRoom selectSpaceRoomByCodeAll(@Param("roomCode") String roomCode);

    public List<SpaceRoom> selectSpaceRoomList(SpaceRoom spaceRoom);

    public List<SpaceRoom> selectDeletedSpaceRoomList(SpaceRoom spaceRoom);

    public int insertSpaceRoom(SpaceRoom spaceRoom);

    public int updateSpaceRoom(SpaceRoom spaceRoom);

    public int deleteSpaceRoomById(Long roomId);

    public int deleteSpaceRoomByIds(Long[] roomIds);

    public int restoreSpaceRoomByIds(Long[] roomIds);

    public int forceDeleteSpaceRoomByIds(Long[] roomIds);
}
