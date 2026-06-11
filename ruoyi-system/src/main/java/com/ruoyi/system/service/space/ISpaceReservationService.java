package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceReservation;

public interface ISpaceReservationService
{
    public SpaceReservation selectSpaceReservationById(Long reservationId);

    public List<SpaceReservation> selectSpaceReservationList(SpaceReservation spaceReservation);

    public int insertSpaceReservation(SpaceReservation spaceReservation);

    public int updateSpaceReservation(SpaceReservation spaceReservation);

    public int deleteSpaceReservationByIds(Long[] reservationIds);

    public int deleteSpaceReservationById(Long reservationId);

    public int approveReservation(Long reservationId, Long auditorId, String auditorName, String opinion, String updateBy);

    public int rejectReservation(Long reservationId, Long auditorId, String auditorName, String reason, String updateBy);

    public int cancelReservation(Long reservationId, String updateBy);

    public int approveCancelReservation(Long reservationId, Long auditorId, String auditorName, String opinion, String updateBy);

    public int rejectCancelReservation(Long reservationId, Long auditorId, String auditorName, String reason, String updateBy);

    public int approveItem(Long itemId, Long auditorId, String auditorName, String opinion, String updateBy);

    public int rejectItem(Long itemId, Long auditorId, String auditorName, String reason, String updateBy);

    public int approveCancelItem(Long itemId, Long auditorId, String auditorName, String opinion, String updateBy);

    public int rejectCancelItem(Long itemId, Long auditorId, String auditorName, String reason, String updateBy);

    public int refreshFinishedReservations();
}
