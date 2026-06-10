package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceReservation;

public interface SpaceReservationMapper
{
    public SpaceReservation selectSpaceReservationById(Long reservationId);
    public List<SpaceReservation> selectSpaceReservationList(SpaceReservation spaceReservation);
    public int insertSpaceReservation(SpaceReservation spaceReservation);
    public int updateSpaceReservation(SpaceReservation spaceReservation);
    public int deleteSpaceReservationById(Long reservationId);
    public int deleteSpaceReservationByIds(Long[] reservationIds);
    public int updateReservationStatus(SpaceReservation reservation);
}