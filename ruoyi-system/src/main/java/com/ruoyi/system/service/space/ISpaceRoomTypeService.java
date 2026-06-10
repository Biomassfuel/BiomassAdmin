package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceRoomType;

public interface ISpaceRoomTypeService
{
    public SpaceRoomType selectSpaceRoomTypeById(Long typeId);
    public List<SpaceRoomType> selectSpaceRoomTypeList(SpaceRoomType spaceRoomType);
    public int insertSpaceRoomType(SpaceRoomType spaceRoomType);
    public int updateSpaceRoomType(SpaceRoomType spaceRoomType);
    public int deleteSpaceRoomTypeByIds(Long[] typeIds);
    public int deleteSpaceRoomTypeById(Long typeId);
}