package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.space.SpaceReservationRule;
import com.ruoyi.system.mapper.space.SpaceReservationRuleMapper;
import com.ruoyi.system.service.space.ISpaceReservationRuleService;

@Service
public class SpaceReservationRuleServiceImpl implements ISpaceReservationRuleService
{
    @Autowired
    private SpaceReservationRuleMapper spaceReservationRuleMapper;

    @Override
    public SpaceReservationRule selectSpaceReservationRuleById(Long ruleId)
    {
        return spaceReservationRuleMapper.selectSpaceReservationRuleById(ruleId);
    }

    @Override
    public List<SpaceReservationRule> selectSpaceReservationRuleList(SpaceReservationRule spaceReservationRule)
    {
        return spaceReservationRuleMapper.selectSpaceReservationRuleList(spaceReservationRule);
    }

    @Override
    public int insertSpaceReservationRule(SpaceReservationRule spaceReservationRule)
    {
        return spaceReservationRuleMapper.insertSpaceReservationRule(spaceReservationRule);
    }

    @Override
    public int updateSpaceReservationRule(SpaceReservationRule spaceReservationRule)
    {
        return spaceReservationRuleMapper.updateSpaceReservationRule(spaceReservationRule);
    }

    @Override
    public int deleteSpaceReservationRuleByIds(Long[] ruleIds)
    {
        return spaceReservationRuleMapper.deleteSpaceReservationRuleByIds(ruleIds);
    }

    @Override
    public int deleteSpaceReservationRuleById(Long ruleId)
    {
        return spaceReservationRuleMapper.deleteSpaceReservationRuleById(ruleId);
    }
}