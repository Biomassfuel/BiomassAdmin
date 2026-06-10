package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceTimePeriod;

public interface ISpaceTimePeriodService
{
    public SpaceTimePeriod selectSpaceTimePeriodById(Long periodId);
    public List<SpaceTimePeriod> selectSpaceTimePeriodList(SpaceTimePeriod spaceTimePeriod);
    public int insertSpaceTimePeriod(SpaceTimePeriod spaceTimePeriod);
    public int updateSpaceTimePeriod(SpaceTimePeriod spaceTimePeriod);
    public int deleteSpaceTimePeriodByIds(Long[] periodIds);
    public int deleteSpaceTimePeriodById(Long periodId);
}