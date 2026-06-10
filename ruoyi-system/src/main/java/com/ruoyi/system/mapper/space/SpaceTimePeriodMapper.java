package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceTimePeriod;

public interface SpaceTimePeriodMapper
{
    public SpaceTimePeriod selectSpaceTimePeriodById(Long periodId);
    public List<SpaceTimePeriod> selectSpaceTimePeriodList(SpaceTimePeriod spaceTimePeriod);
    public int insertSpaceTimePeriod(SpaceTimePeriod spaceTimePeriod);
    public int updateSpaceTimePeriod(SpaceTimePeriod spaceTimePeriod);
    public int deleteSpaceTimePeriodById(Long periodId);
    public int deleteSpaceTimePeriodByIds(Long[] periodIds);
}