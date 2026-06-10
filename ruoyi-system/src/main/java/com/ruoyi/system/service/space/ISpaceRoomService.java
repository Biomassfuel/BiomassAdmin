package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceRoom;

public interface ISpaceRoomService
{
    public SpaceRoom selectSpaceRoomById(Long roomId);

    public List<SpaceRoom> selectSpaceRoomList(SpaceRoom spaceRoom);

    public int insertSpaceRoom(SpaceRoom spaceRoom);

    public int updateSpaceRoom(SpaceRoom spaceRoom);

    public int deleteSpaceRoomByIds(Long[] roomIds);

    public int deleteSpaceRoomById(Long roomId);

    public String importRoom(List<SpaceRoom> roomList, Boolean isUpdateSupport, String operName, String fileName);
}
