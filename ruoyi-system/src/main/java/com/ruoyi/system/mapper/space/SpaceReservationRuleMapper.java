package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceReservationRule;

public interface SpaceReservationRuleMapper
{
    public SpaceReservationRule selectSpaceReservationRuleById(Long ruleId);
    public List<SpaceReservationRule> selectSpaceReservationRuleList(SpaceReservationRule spaceReservationRule);
    public int insertSpaceReservationRule(SpaceReservationRule spaceReservationRule);
    public int updateSpaceReservationRule(SpaceReservationRule spaceReservationRule);
    public int deleteSpaceReservationRuleById(Long ruleId);
    public int deleteSpaceReservationRuleByIds(Long[] ruleIds);
}