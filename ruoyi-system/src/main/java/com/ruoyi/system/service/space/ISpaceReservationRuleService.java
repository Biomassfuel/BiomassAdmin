package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceReservationRule;

public interface ISpaceReservationRuleService
{
    public SpaceReservationRule selectSpaceReservationRuleById(Long ruleId);
    public List<SpaceReservationRule> selectSpaceReservationRuleList(SpaceReservationRule spaceReservationRule);
    public int insertSpaceReservationRule(SpaceReservationRule spaceReservationRule);
    public int updateSpaceReservationRule(SpaceReservationRule spaceReservationRule);
    public int deleteSpaceReservationRuleByIds(Long[] ruleIds);
    public int deleteSpaceReservationRuleById(Long ruleId);
}