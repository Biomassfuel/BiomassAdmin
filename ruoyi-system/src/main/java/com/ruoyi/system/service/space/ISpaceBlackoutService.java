package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceBlackout;

public interface ISpaceBlackoutService
{
    public SpaceBlackout selectSpaceBlackoutById(Long blackoutId);
    public List<SpaceBlackout> selectSpaceBlackoutList(SpaceBlackout spaceBlackout);
    public int insertSpaceBlackout(SpaceBlackout spaceBlackout);
    public int updateSpaceBlackout(SpaceBlackout spaceBlackout);
    public int deleteSpaceBlackoutByIds(Long[] blackoutIds);
    public int deleteSpaceBlackoutById(Long blackoutId);
}