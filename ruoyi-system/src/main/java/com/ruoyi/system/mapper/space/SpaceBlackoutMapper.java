package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceBlackout;

public interface SpaceBlackoutMapper
{
    public SpaceBlackout selectSpaceBlackoutById(Long blackoutId);
    public List<SpaceBlackout> selectSpaceBlackoutList(SpaceBlackout spaceBlackout);
    public int insertSpaceBlackout(SpaceBlackout spaceBlackout);
    public int updateSpaceBlackout(SpaceBlackout spaceBlackout);
    public int deleteSpaceBlackoutById(Long blackoutId);
    public int deleteSpaceBlackoutByIds(Long[] blackoutIds);
}