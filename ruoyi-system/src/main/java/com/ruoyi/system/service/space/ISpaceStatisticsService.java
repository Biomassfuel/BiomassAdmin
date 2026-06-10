package com.ruoyi.system.service.space;

import java.util.List;

import com.ruoyi.system.domain.space.SpaceRoomStatistics;
import com.ruoyi.system.domain.space.SpaceStatisticsDashboard;
import com.ruoyi.system.domain.space.SpaceStatisticsQuery;

public interface ISpaceStatisticsService
{
    public SpaceStatisticsDashboard selectDashboard(SpaceStatisticsQuery query);

    public List<SpaceRoomStatistics> selectRoomStatistics(SpaceStatisticsQuery query);
}
