package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceOrg;

public interface SpaceOrgMapper
{
    public SpaceOrg selectSpaceOrgById(Long orgId);
    public List<SpaceOrg> selectSpaceOrgList(SpaceOrg spaceOrg);
    public int insertSpaceOrg(SpaceOrg spaceOrg);
    public int updateSpaceOrg(SpaceOrg spaceOrg);
    public int deleteSpaceOrgById(Long orgId);
    public int deleteSpaceOrgByIds(Long[] orgIds);
}