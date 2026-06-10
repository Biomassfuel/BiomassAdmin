package com.ruoyi.system.domain.space;

import java.util.List;

public class SpaceStatisticsDashboard
{
    private SpaceStatisticsSummary summary;
    private List<SpaceRoomStatistics> roomRanking;
    private List<SpaceStatisticsTrend> dailyTrend;

    public SpaceStatisticsSummary getSummary()
    {
        return summary;
    }

    public void setSummary(SpaceStatisticsSummary summary)
    {
        this.summary = summary;
    }

    public List<SpaceRoomStatistics> getRoomRanking()
    {
        return roomRanking;
    }

    public void setRoomRanking(List<SpaceRoomStatistics> roomRanking)
    {
        this.roomRanking = roomRanking;
    }

    public List<SpaceStatisticsTrend> getDailyTrend()
    {
        return dailyTrend;
    }

    public void setDailyTrend(List<SpaceStatisticsTrend> dailyTrend)
    {
        this.dailyTrend = dailyTrend;
    }
}
