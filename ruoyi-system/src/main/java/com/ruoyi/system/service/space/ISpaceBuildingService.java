package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceBuilding;

public interface ISpaceBuildingService
{
    public SpaceBuilding selectSpaceBuildingById(Long buildingId);
    public List<SpaceBuilding> selectSpaceBuildingList(SpaceBuilding spaceBuilding);
    public int insertSpaceBuilding(SpaceBuilding spaceBuilding);
    public int updateSpaceBuilding(SpaceBuilding spaceBuilding);
    public int deleteSpaceBuildingByIds(Long[] buildingIds);
    public int deleteSpaceBuildingById(Long buildingId);
}