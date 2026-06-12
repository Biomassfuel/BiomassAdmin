package com.ruoyi.system.service.space.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.space.SpaceRoomStatistics;
import com.ruoyi.system.domain.space.SpaceStatisticsDashboard;
import com.ruoyi.system.domain.space.SpaceStatisticsQuery;
import com.ruoyi.system.mapper.space.SpaceStatisticsMapper;
import com.ruoyi.system.service.space.ISpaceStatisticsService;

@Service
public class SpaceStatisticsServiceImpl implements ISpaceStatisticsService
{
    @Autowired
    private SpaceStatisticsMapper spaceStatisticsMapper;

    @Override
    public SpaceStatisticsDashboard selectDashboard(SpaceStatisticsQuery query)
    {
        query = normalizeQuery(query);
        SpaceStatisticsDashboard dashboard = new SpaceStatisticsDashboard();
        dashboard.setSummary(spaceStatisticsMapper.selectSummary(query));
        dashboard.setDailyTrend(spaceStatisticsMapper.selectDailyTrend(query));
        return dashboard;
    }

    @Override
    public List<SpaceRoomStatistics> selectRoomStatistics(SpaceStatisticsQuery query)
    {
        query = normalizeQuery(query);
        return spaceStatisticsMapper.selectRoomStatistics(query);
    }

    private SpaceStatisticsQuery normalizeQuery(SpaceStatisticsQuery query)
    {
        if (query == null)
        {
            query = new SpaceStatisticsQuery();
        }
        LocalDate today = LocalDate.now();
        if (StringUtils.isEmpty(query.getStartDate()))
        {
            query.setStartDate(today.minusDays(29).toString());
        }
        if (StringUtils.isEmpty(query.getEndDate()))
        {
            query.setEndDate(today.toString());
        }
        return query;
    }
}
