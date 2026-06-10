package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.space.SpaceOrg;
import com.ruoyi.system.mapper.space.SpaceOrgMapper;
import com.ruoyi.system.service.space.ISpaceOrgService;

@Service
public class SpaceOrgServiceImpl implements ISpaceOrgService
{
    @Autowired
    private SpaceOrgMapper spaceOrgMapper;

    @Override
    public SpaceOrg selectSpaceOrgById(Long orgId)
    {
        return spaceOrgMapper.selectSpaceOrgById(orgId);
    }

    @Override
    public List<SpaceOrg> selectSpaceOrgList(SpaceOrg spaceOrg)
    {
        return spaceOrgMapper.selectSpaceOrgList(spaceOrg);
    }

    @Override
    public int insertSpaceOrg(SpaceOrg spaceOrg)
    {
        return spaceOrgMapper.insertSpaceOrg(spaceOrg);
    }

    @Override
    public int updateSpaceOrg(SpaceOrg spaceOrg)
    {
        return spaceOrgMapper.updateSpaceOrg(spaceOrg);
    }

    @Override
    public int deleteSpaceOrgByIds(Long[] orgIds)
    {
        return spaceOrgMapper.deleteSpaceOrgByIds(orgIds);
    }

    @Override
    public int deleteSpaceOrgById(Long orgId)
    {
        return spaceOrgMapper.deleteSpaceOrgById(orgId);
    }
}