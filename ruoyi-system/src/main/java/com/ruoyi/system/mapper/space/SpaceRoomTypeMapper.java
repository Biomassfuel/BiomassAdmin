package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceRoomType;

public interface SpaceRoomTypeMapper
{
    public SpaceRoomType selectSpaceRoomTypeById(Long typeId);

    public SpaceRoomType selectSpaceRoomTypeByCode(@Param("typeCode") String typeCode);
    public List<SpaceRoomType> selectSpaceRoomTypeList(SpaceRoomType spaceRoomType);
    public int insertSpaceRoomType(SpaceRoomType spaceRoomType);
    public int updateSpaceRoomType(SpaceRoomType spaceRoomType);
    public int deleteSpaceRoomTypeById(Long typeId);
    public int deleteSpaceRoomTypeByIds(Long[] typeIds);
}
