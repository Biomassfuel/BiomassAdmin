package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceBuilding;

public interface SpaceBuildingMapper
{
    public SpaceBuilding selectSpaceBuildingById(Long buildingId);
    public List<SpaceBuilding> selectSpaceBuildingList(SpaceBuilding spaceBuilding);
    public int insertSpaceBuilding(SpaceBuilding spaceBuilding);
    public int updateSpaceBuilding(SpaceBuilding spaceBuilding);
    public int deleteSpaceBuildingById(Long buildingId);
    public int deleteSpaceBuildingByIds(Long[] buildingIds);
}