package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceOrg;

public interface ISpaceOrgService
{
    public SpaceOrg selectSpaceOrgById(Long orgId);
    public List<SpaceOrg> selectSpaceOrgList(SpaceOrg spaceOrg);
    public int insertSpaceOrg(SpaceOrg spaceOrg);
    public int updateSpaceOrg(SpaceOrg spaceOrg);
    public int deleteSpaceOrgByIds(Long[] orgIds);
    public int deleteSpaceOrgById(Long orgId);
}