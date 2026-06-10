package com.ruoyi.system.mapper.space;

import java.util.List;

import com.ruoyi.system.domain.space.SpaceRoomStatistics;
import com.ruoyi.system.domain.space.SpaceStatisticsQuery;
import com.ruoyi.system.domain.space.SpaceStatisticsSummary;
import com.ruoyi.system.domain.space.SpaceStatisticsTrend;

public interface SpaceStatisticsMapper
{
    public SpaceStatisticsSummary selectSummary(SpaceStatisticsQuery query);

    public List<SpaceRoomStatistics> selectRoomStatistics(SpaceStatisticsQuery query);

    public List<SpaceStatisticsTrend> selectDailyTrend(SpaceStatisticsQuery query);
}
