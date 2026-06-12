package com.ruoyi.system.service.space.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.space.SpaceAuditLog;
import com.ruoyi.system.domain.space.SpaceReservation;
import com.ruoyi.system.domain.space.SpaceReservationItem;
import com.ruoyi.system.domain.space.SpaceReservationRule;
import com.ruoyi.system.domain.space.SpaceRoom;
import com.ruoyi.system.domain.space.SpaceTimePeriod;
import com.ruoyi.system.mapper.space.SpaceAuditLogMapper;
import com.ruoyi.system.mapper.space.SpaceReservationItemMapper;
import com.ruoyi.system.mapper.space.SpaceReservationMapper;
import com.ruoyi.system.mapper.space.SpaceReservationRuleMapper;
import com.ruoyi.system.mapper.space.SpaceRoomDayLockMapper;
import com.ruoyi.system.mapper.space.SpaceRoomMapper;
import com.ruoyi.system.mapper.space.SpaceTimePeriodMapper;
import com.ruoyi.system.service.space.ISpaceReservationService;

@Service
public class SpaceReservationServiceImpl implements ISpaceReservationService
{
    private static final String AUDIT_TYPE_NORMAL = "0";

    private static final String AUDIT_TYPE_CANCEL = "1";

    private static final DateTimeFormatter BOOKING_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter BOOKING_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private SpaceReservationMapper spaceReservationMapper;

    @Autowired
    private SpaceReservationItemMapper spaceReservationItemMapper;

    @Autowired
    private SpaceReservationRuleMapper spaceReservationRuleMapper;

    @Autowired
    private SpaceAuditLogMapper spaceAuditLogMapper;

    @Autowired
    private SpaceRoomMapper spaceRoomMapper;

    @Autowired
    private SpaceRoomDayLockMapper spaceRoomDayLockMapper;

    @Autowired
    private SpaceTimePeriodMapper spaceTimePeriodMapper;

    @Override
    public SpaceReservation selectSpaceReservationById(Long reservationId)
    {
        SpaceReservation reservation = spaceReservationMapper.selectSpaceReservationById(reservationId);
        if (reservation == null)
        {
            return null;
        }
        SpaceReservationItem itemQuery = new SpaceReservationItem();
        itemQuery.setReservationId(reservationId);
        reservation.setItems(spaceReservationItemMapper.selectSpaceReservationItemList(itemQuery));

        SpaceReservationRule ruleQuery = new SpaceReservationRule();
        ruleQuery.setReservationId(reservationId);
        List<SpaceReservationRule> rules = spaceReservationRuleMapper.selectSpaceReservationRuleList(ruleQuery);
        if (rules != null && !rules.isEmpty())
        {
            reservation.setRule(rules.get(0));
        }
        return reservation;
    }

    @Override
    public List<SpaceReservation> selectSpaceReservationList(SpaceReservation spaceReservation)
    {
        return spaceReservationMapper.selectSpaceReservationList(spaceReservation);
    }

    @Override
    public List<SpaceReservation> selectPublicReservationSummaryList(SpaceReservation spaceReservation)
    {
        return spaceReservationMapper.selectPublicReservationSummaryList(spaceReservation);
    }

    @Override
    @Transactional
    public int insertSpaceReservation(SpaceReservation spaceReservation)
    {
        List<SpaceReservationItem> items = spaceReservation.getItems();
        if (items == null || items.isEmpty())
        {
            throw new ServiceException("请至少选择一个预约场次");
        }
        if (StringUtils.isEmpty(spaceReservation.getReservationNo()))
        {
            spaceReservation.setReservationNo("YY" + System.currentTimeMillis());
        }
        if (StringUtils.isEmpty(spaceReservation.getReservationType()))
        {
            spaceReservation.setReservationType("0");
        }

        for (SpaceReservationItem item : items)
        {
            fillRoomSnapshot(item);
            assertNoConflict(item, spaceReservation.getCreateBy());
        }

        spaceReservation.setStatus("1");
        spaceReservation.setAuditType(AUDIT_TYPE_NORMAL);
        spaceReservation.setDelFlag("0");
        spaceReservation.setSubmitTime(new Date());
        int rows = spaceReservationMapper.insertSpaceReservation(spaceReservation);

        if (spaceReservation.getRule() != null)
        {
            SpaceReservationRule rule = spaceReservation.getRule();
            rule.setReservationId(spaceReservation.getReservationId());
            rule.setCreateBy(spaceReservation.getCreateBy());
            spaceReservationRuleMapper.insertSpaceReservationRule(rule);
        }

        for (SpaceReservationItem item : items)
        {
            item.setReservationId(spaceReservation.getReservationId());
            item.setCreateBy(spaceReservation.getCreateBy());
            item.setItemStatus("1");
            item.setConflictFlag("0");
            item.setConflictReason("");
            item.setConflictItemId(null);
            spaceReservationItemMapper.insertSpaceReservationItem(item);
        }
        writeAuditLog(spaceReservation.getReservationId(), null, "0", "", "1", spaceReservation.getApplicantId(), spaceReservation.getApplicantName(), "提交预约申请", spaceReservation.getCreateBy());
        return rows;
    }

    @Override
    public int updateSpaceReservation(SpaceReservation spaceReservation)
    {
        return spaceReservationMapper.updateSpaceReservation(spaceReservation);
    }

    @Override
    public int deleteSpaceReservationByIds(Long[] reservationIds)
    {
        return spaceReservationMapper.deleteSpaceReservationByIds(reservationIds);
    }

    @Override
    public int deleteSpaceReservationById(Long reservationId)
    {
        return spaceReservationMapper.deleteSpaceReservationById(reservationId);
    }

    @Override
    @Transactional
    public int refreshFinishedReservations()
    {
        List<Long> reservationIds = spaceReservationItemMapper.selectReservationIdsToFinish();
        if (reservationIds == null || reservationIds.isEmpty())
        {
            return 0;
        }
        int rows = spaceReservationItemMapper.updateFinishedItems(reservationIds);
        for (Long reservationId : reservationIds)
        {
            refreshFinishedReservationStatus(reservationId);
        }
        return rows;
    }

    @Override
    @Transactional
    public int approveReservation(Long reservationId, Long auditorId, String auditorName, String opinion, String updateBy)
    {
        SpaceReservation reservation = selectSpaceReservationById(reservationId);
        if (reservation == null)
        {
            throw new ServiceException("预约申请不存在");
        }
        if (AUDIT_TYPE_CANCEL.equals(reservation.getAuditType()))
        {
            throw new ServiceException("取消审核预约请在待取消审核模块处理");
        }
        for (SpaceReservationItem item : reservation.getItems())
        {
            if ("1".equals(item.getConflictFlag()) || "4".equals(item.getItemStatus()))
            {
                throw new ServiceException("存在冲突场次，请在冲突预约处理或详情中逐项处理");
            }
        }
        for (SpaceReservationItem item : reservation.getItems())
        {
            assertNoConflict(item, updateBy);
            item.setItemStatus("2");
            item.setConflictFlag("0");
            item.setConflictReason("");
            item.setAuditorId(auditorId);
            item.setAuditorName(auditorName);
            item.setRejectReason("");
            item.setUpdateBy(updateBy);
            spaceReservationItemMapper.updateItemStatus(item);
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus("2");
        update.setAuditType(AUDIT_TYPE_NORMAL);
        update.setAuditorId(auditorId);
        update.setAuditorName(auditorName);
        update.setRejectReason("");
        update.setUpdateBy(updateBy);
        int rows = spaceReservationMapper.updateReservationStatus(update);
        writeAuditLog(reservationId, null, "1", reservation.getStatus(), "2", auditorId, auditorName, opinion, updateBy);
        return rows;
    }

    @Override
    @Transactional
    public int rejectReservation(Long reservationId, Long auditorId, String auditorName, String reason, String updateBy)
    {
        SpaceReservation reservation = selectSpaceReservationById(reservationId);
        if (reservation == null)
        {
            throw new ServiceException("预约申请不存在");
        }
        if (AUDIT_TYPE_CANCEL.equals(reservation.getAuditType()))
        {
            throw new ServiceException("取消审核预约请在待取消审核模块处理");
        }
        for (SpaceReservationItem item : reservation.getItems())
        {
            item.setItemStatus("3");
            item.setConflictFlag("0");
            item.setConflictReason("");
            item.setAuditorId(auditorId);
            item.setAuditorName(auditorName);
            item.setRejectReason(reason);
            item.setUpdateBy(updateBy);
            spaceReservationItemMapper.updateItemStatus(item);
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus("4");
        update.setAuditType(AUDIT_TYPE_NORMAL);
        update.setAuditorId(auditorId);
        update.setAuditorName(auditorName);
        update.setRejectReason(reason);
        update.setUpdateBy(updateBy);
        int rows = spaceReservationMapper.updateReservationStatus(update);
        writeAuditLog(reservationId, null, "2", reservation.getStatus(), "4", auditorId, auditorName, reason, updateBy);
        return rows;
    }

    @Override
    @Transactional
    public int cancelReservation(Long reservationId, String updateBy)
    {
        SpaceReservation reservation = selectSpaceReservationById(reservationId);
        if (reservation == null)
        {
            throw new ServiceException("预约申请不存在");
        }
        if (AUDIT_TYPE_CANCEL.equals(reservation.getAuditType()))
        {
            throw new ServiceException("取消申请正在审核中");
        }
        if ("2".equals(reservation.getStatus()) || "3".equals(reservation.getStatus()))
        {
            return submitCancelAudit(reservation, updateBy);
        }
        if (!"1".equals(reservation.getStatus()))
        {
            throw new ServiceException("当前状态不支持取消");
        }
        for (SpaceReservationItem item : reservation.getItems())
        {
            item.setItemStatus("5");
            item.setConflictFlag("0");
            item.setConflictReason("");
            item.setUpdateBy(updateBy);
            spaceReservationItemMapper.updateItemStatus(item);
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus("5");
        update.setAuditType(AUDIT_TYPE_NORMAL);
        update.setUpdateBy(updateBy);
        int rows = spaceReservationMapper.updateReservationStatus(update);
        writeAuditLog(reservationId, null, "4", reservation.getStatus(), "5", null, updateBy, "取消预约申请", updateBy);
        return rows;
    }

    @Override
    @Transactional
    public int approveCancelReservation(Long reservationId, Long auditorId, String auditorName, String opinion, String updateBy)
    {
        SpaceReservation reservation = selectCancelAuditReservation(reservationId);
        for (SpaceReservationItem item : reservation.getItems())
        {
            if ("1".equals(item.getItemStatus()))
            {
                item.setItemStatus("5");
                item.setConflictFlag("0");
                item.setConflictReason("");
                item.setAuditorId(auditorId);
                item.setAuditorName(auditorName);
                item.setRejectReason("");
                item.setUpdateBy(updateBy);
                spaceReservationItemMapper.updateItemStatus(item);
            }
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus(resolveCancelAuditFinishedStatus(reservationId));
        update.setAuditType(AUDIT_TYPE_NORMAL);
        update.setAuditorId(auditorId);
        update.setAuditorName(auditorName);
        update.setRejectReason("");
        update.setUpdateBy(updateBy);
        int rows = spaceReservationMapper.updateReservationStatus(update);
        writeAuditLog(reservationId, null, "8", reservation.getStatus(), update.getStatus(), auditorId, auditorName, opinion, updateBy);
        return rows;
    }

    @Override
    @Transactional
    public int rejectCancelReservation(Long reservationId, Long auditorId, String auditorName, String reason, String updateBy)
    {
        SpaceReservation reservation = selectCancelAuditReservation(reservationId);
        for (SpaceReservationItem item : reservation.getItems())
        {
            if ("1".equals(item.getItemStatus()))
            {
                item.setItemStatus("2");
                item.setConflictFlag("0");
                item.setConflictReason("");
                item.setAuditorId(auditorId);
                item.setAuditorName(auditorName);
                item.setRejectReason(reason);
                item.setUpdateBy(updateBy);
                spaceReservationItemMapper.updateItemStatus(item);
            }
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus(resolveCancelAuditFinishedStatus(reservationId));
        update.setAuditType(AUDIT_TYPE_NORMAL);
        update.setAuditorId(auditorId);
        update.setAuditorName(auditorName);
        update.setRejectReason(reason);
        update.setUpdateBy(updateBy);
        int rows = spaceReservationMapper.updateReservationStatus(update);
        writeAuditLog(reservationId, null, "9", reservation.getStatus(), update.getStatus(), auditorId, auditorName, reason, updateBy);
        return rows;
    }

    @Override
    @Transactional
    public int approveItem(Long itemId, Long auditorId, String auditorName, String opinion, String updateBy)
    {
        SpaceReservationItem item = spaceReservationItemMapper.selectSpaceReservationItemById(itemId);
        if (item == null)
        {
            throw new ServiceException("预约场次不存在");
        }
        SpaceReservation reservation = selectSpaceReservationById(item.getReservationId());
        if (reservation != null && AUDIT_TYPE_CANCEL.equals(reservation.getAuditType()))
        {
            throw new ServiceException("取消审核场次请在待取消审核模块处理");
        }
        assertNoConflict(item, updateBy);
        String before = item.getItemStatus();
        item.setItemStatus("2");
        item.setConflictFlag("0");
        item.setConflictReason("");
        item.setAuditorId(auditorId);
        item.setAuditorName(auditorName);
        item.setRejectReason("");
        item.setUpdateBy(updateBy);
        int rows = spaceReservationItemMapper.updateItemStatus(item);
        writeAuditLog(item.getReservationId(), itemId, "5", before, "2", auditorId, auditorName, opinion, updateBy);
        refreshReservationStatus(item.getReservationId(), auditorId, auditorName, updateBy);
        return rows;
    }

    @Override
    @Transactional
    public int rejectItem(Long itemId, Long auditorId, String auditorName, String reason, String updateBy)
    {
        SpaceReservationItem item = spaceReservationItemMapper.selectSpaceReservationItemById(itemId);
        if (item == null)
        {
            throw new ServiceException("预约场次不存在");
        }
        SpaceReservation reservation = selectSpaceReservationById(item.getReservationId());
        if (reservation != null && AUDIT_TYPE_CANCEL.equals(reservation.getAuditType()))
        {
            throw new ServiceException("取消审核场次请在待取消审核模块处理");
        }
        String before = item.getItemStatus();
        item.setItemStatus("3");
        item.setConflictFlag("0");
        item.setConflictReason("");
        item.setAuditorId(auditorId);
        item.setAuditorName(auditorName);
        item.setRejectReason(reason);
        item.setUpdateBy(updateBy);
        int rows = spaceReservationItemMapper.updateItemStatus(item);
        writeAuditLog(item.getReservationId(), itemId, "6", before, "3", auditorId, auditorName, reason, updateBy);
        refreshReservationStatus(item.getReservationId(), auditorId, auditorName, updateBy);
        return rows;
    }

    @Override
    @Transactional
    public int approveCancelItem(Long itemId, Long auditorId, String auditorName, String opinion, String updateBy)
    {
        SpaceReservationItem item = selectCancelAuditItem(itemId);
        String before = item.getItemStatus();
        item.setItemStatus("5");
        item.setConflictFlag("0");
        item.setConflictReason("");
        item.setAuditorId(auditorId);
        item.setAuditorName(auditorName);
        item.setRejectReason("");
        item.setUpdateBy(updateBy);
        int rows = spaceReservationItemMapper.updateItemStatus(item);
        writeAuditLog(item.getReservationId(), itemId, "A", before, "5", auditorId, auditorName, opinion, updateBy);
        refreshCancelAuditReservationStatus(item.getReservationId(), auditorId, auditorName, updateBy, "");
        return rows;
    }

    @Override
    @Transactional
    public int rejectCancelItem(Long itemId, Long auditorId, String auditorName, String reason, String updateBy)
    {
        SpaceReservationItem item = selectCancelAuditItem(itemId);
        String before = item.getItemStatus();
        item.setItemStatus("2");
        item.setConflictFlag("0");
        item.setConflictReason("");
        item.setAuditorId(auditorId);
        item.setAuditorName(auditorName);
        item.setRejectReason(reason);
        item.setUpdateBy(updateBy);
        int rows = spaceReservationItemMapper.updateItemStatus(item);
        writeAuditLog(item.getReservationId(), itemId, "B", before, "2", auditorId, auditorName, reason, updateBy);
        refreshCancelAuditReservationStatus(item.getReservationId(), auditorId, auditorName, updateBy, reason);
        return rows;
    }

    private int submitCancelAudit(SpaceReservation reservation, String updateBy)
    {
        boolean hasApprovedItem = false;
        for (SpaceReservationItem item : reservation.getItems())
        {
            if ("2".equals(item.getItemStatus()))
            {
                hasApprovedItem = true;
                item.setItemStatus("1");
                item.setConflictFlag("0");
                item.setConflictReason("");
                item.setAuditorId(null);
                item.setAuditorName("");
                item.setRejectReason("");
                item.setUpdateBy(updateBy);
                spaceReservationItemMapper.updateItemStatus(item);
            }
        }
        if (!hasApprovedItem)
        {
            throw new ServiceException("当前预约没有可取消的已通过场次");
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservation.getReservationId());
        update.setStatus("1");
        update.setAuditType(AUDIT_TYPE_CANCEL);
        update.setAuditorId(null);
        update.setAuditorName("");
        update.setRejectReason("");
        update.setUpdateBy(updateBy);
        int rows = spaceReservationMapper.updateReservationStatus(update);
        writeAuditLog(reservation.getReservationId(), null, "7", reservation.getStatus(), "1", null, updateBy, "发起取消审核", updateBy);
        return rows;
    }

    private SpaceReservation selectCancelAuditReservation(Long reservationId)
    {
        SpaceReservation reservation = selectSpaceReservationById(reservationId);
        if (reservation == null)
        {
            throw new ServiceException("预约申请不存在");
        }
        if (!"1".equals(reservation.getStatus()) || !AUDIT_TYPE_CANCEL.equals(reservation.getAuditType()))
        {
            throw new ServiceException("当前预约不在取消审核中");
        }
        return reservation;
    }

    private SpaceReservationItem selectCancelAuditItem(Long itemId)
    {
        SpaceReservationItem item = spaceReservationItemMapper.selectSpaceReservationItemById(itemId);
        if (item == null)
        {
            throw new ServiceException("预约场次不存在");
        }
        SpaceReservation reservation = selectCancelAuditReservation(item.getReservationId());
        if (!"1".equals(item.getItemStatus()))
        {
            throw new ServiceException("当前场次不在取消审核中");
        }
        if (reservation.getItems() == null)
        {
            throw new ServiceException("当前预约不在取消审核中");
        }
        return item;
    }

    private void refreshCancelAuditReservationStatus(Long reservationId, Long auditorId, String auditorName, String updateBy, String rejectReason)
    {
        SpaceReservationItem query = new SpaceReservationItem();
        query.setReservationId(reservationId);
        List<SpaceReservationItem> items = spaceReservationItemMapper.selectSpaceReservationItemList(query);
        for (SpaceReservationItem item : items)
        {
            if ("1".equals(item.getItemStatus()))
            {
                SpaceReservation update = new SpaceReservation();
                update.setReservationId(reservationId);
                update.setStatus("1");
                update.setAuditType(AUDIT_TYPE_CANCEL);
                update.setAuditorId(auditorId);
                update.setAuditorName(auditorName);
                update.setRejectReason(rejectReason);
                update.setUpdateBy(updateBy);
                spaceReservationMapper.updateReservationStatus(update);
                return;
            }
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus(resolveFinishedStatus(items));
        update.setAuditType(AUDIT_TYPE_NORMAL);
        update.setAuditorId(auditorId);
        update.setAuditorName(auditorName);
        update.setRejectReason(rejectReason);
        update.setUpdateBy(updateBy);
        spaceReservationMapper.updateReservationStatus(update);
    }

    private String resolveCancelAuditFinishedStatus(Long reservationId)
    {
        SpaceReservationItem query = new SpaceReservationItem();
        query.setReservationId(reservationId);
        return resolveFinishedStatus(spaceReservationItemMapper.selectSpaceReservationItemList(query));
    }

    private String resolveFinishedStatus(List<SpaceReservationItem> items)
    {
        if (items == null || items.isEmpty())
        {
            return "3";
        }
        int approved = 0;
        int rejected = 0;
        int canceled = 0;
        int finished = 0;
        int pending = 0;
        for (SpaceReservationItem item : items)
        {
            if ("2".equals(item.getItemStatus()))
            {
                approved++;
            }
            else if ("3".equals(item.getItemStatus()))
            {
                rejected++;
            }
            else if ("5".equals(item.getItemStatus()))
            {
                canceled++;
            }
            else if ("6".equals(item.getItemStatus()))
            {
                finished++;
            }
            else if ("1".equals(item.getItemStatus()) || "4".equals(item.getItemStatus()))
            {
                pending++;
            }
        }
        if (canceled == items.size())
        {
            return "5";
        }
        if (approved == 0 && canceled > 0 && finished == 0)
        {
            return "5";
        }
        if (approved == items.size())
        {
            return "2";
        }
        if (rejected == items.size())
        {
            return "4";
        }
        if (approved > 0)
        {
            return rejected > 0 || canceled > 0 || pending > 0 ? "3" : "2";
        }
        if (finished > 0 && pending == 0)
        {
            return rejected > 0 ? "3" : "6";
        }
        if (approved == 0 && rejected == 0 && canceled == 0 && finished == 0 && pending > 0)
        {
            return "1";
        }
        return "3";
    }

    private void refreshFinishedReservationStatus(Long reservationId)
    {
        SpaceReservationItem query = new SpaceReservationItem();
        query.setReservationId(reservationId);
        List<SpaceReservationItem> items = spaceReservationItemMapper.selectSpaceReservationItemList(query);
        if (items == null || items.isEmpty())
        {
            return;
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus(resolveFinishedStatus(items));
        update.setUpdateBy("system");
        spaceReservationMapper.updateReservationStatus(update);
    }

    private void fillRoomSnapshot(SpaceReservationItem item)
    {
        SpaceRoom room = spaceRoomMapper.selectSpaceRoomById(item.getRoomId());
        if (room == null)
        {
            throw new ServiceException("房间不存在");
        }
        if (!"0".equals(room.getDelFlag()))
        {
            throw new ServiceException("房间已删除，不能预约：" + room.getRoomCode());
        }
        if (!"0".equals(room.getStatus()) || !"0".equals(room.getBookable()))
        {
            throw new ServiceException("房间不可预约或已停用：" + room.getRoomCode());
        }
        item.setRoomCode(room.getRoomCode());
        item.setRoomName(room.getRoomName());
    }

    private void assertNoConflict(SpaceReservationItem item, String operator)
    {
        if (item.getRoomId() == null || StringUtils.isEmpty(item.getBookingDate()) || StringUtils.isEmpty(item.getStartTime()) || StringUtils.isEmpty(item.getEndTime()))
        {
            throw new ServiceException("预约房间、日期、开始时间和结束时间不能为空");
        }
        assertStandardPeriod(item);
        assertNotStarted(item);
        spaceRoomDayLockMapper.insertIgnore(item.getRoomId(), item.getBookingDate(), operator);
        spaceRoomDayLockMapper.lockRoomDay(item.getRoomId(), item.getBookingDate());
        SpaceReservationItem conflict = selectFirstConflict(item);
        if (conflict != null)
        {
            throw new ServiceException("预约时间冲突：" + buildConflictReason(conflict));
        }
    }

    private SpaceReservationItem selectFirstConflict(SpaceReservationItem item)
    {
        List<SpaceReservationItem> conflicts = spaceReservationItemMapper.selectConflictItems(item);
        return conflicts == null || conflicts.isEmpty() ? null : conflicts.get(0);
    }

    private String buildConflictReason(SpaceReservationItem conflict)
    {
        return conflict.getRoomCode() + " " + conflict.getBookingDate() + " " + conflict.getStartTime() + "-" + conflict.getEndTime();
    }

    private void assertStandardPeriod(SpaceReservationItem item)
    {
        SpaceTimePeriod query = new SpaceTimePeriod();
        query.setStatus("0");
        List<SpaceTimePeriod> periods = spaceTimePeriodMapper.selectSpaceTimePeriodList(query);
        for (SpaceTimePeriod period : periods)
        {
            if (isStandardPeriod(period) && sameTime(period.getStartTime(), item.getStartTime()) && sameTime(period.getEndTime(), item.getEndTime()))
            {
                return;
            }
        }
        throw new ServiceException("预约时间必须选择标准时段：上午、下午或晚间");
    }

    private void assertNotStarted(SpaceReservationItem item)
    {
        try
        {
            LocalDate bookingDate = LocalDate.parse(item.getBookingDate(), BOOKING_DATE_FORMATTER);
            LocalTime startTime = LocalTime.parse(normalizeBookingTime(item.getStartTime()), BOOKING_TIME_FORMATTER);
            if (!LocalDateTime.of(bookingDate, startTime).isAfter(LocalDateTime.now()))
            {
                throw new ServiceException("不能预约当前时间之前的场次");
            }
        }
        catch (DateTimeParseException e)
        {
            throw new ServiceException("预约日期或开始时间格式不正确");
        }
    }

    private String normalizeBookingTime(String time)
    {
        String normalized = normalizeTime(time);
        if (normalized.length() == 4)
        {
            return "0" + normalized + ":00";
        }
        if (normalized.length() == 5)
        {
            return normalized + ":00";
        }
        return normalized.length() == 7 ? "0" + normalized : normalized;
    }

    private boolean isStandardPeriod(SpaceTimePeriod period)
    {
        String code = period.getPeriodCode();
        String name = period.getPeriodName();
        return "MORNING".equals(code) || "AFTERNOON".equals(code) || "EVENING".equals(code)
                || "上午".equals(name) || "下午".equals(name) || "晚间".equals(name);
    }

    private boolean sameTime(String standardTime, String inputTime)
    {
        if (standardTime == null || inputTime == null)
        {
            return false;
        }
        return normalizeTime(standardTime).equals(normalizeTime(inputTime));
    }

    private String normalizeTime(String time)
    {
        return time.length() >= 8 ? time.substring(0, 8) : time;
    }

    private void refreshReservationStatus(Long reservationId, Long auditorId, String auditorName, String updateBy)
    {
        SpaceReservationItem query = new SpaceReservationItem();
        query.setReservationId(reservationId);
        List<SpaceReservationItem> items = spaceReservationItemMapper.selectSpaceReservationItemList(query);
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus(resolveFinishedStatus(items));
        update.setAuditType(AUDIT_TYPE_NORMAL);
        update.setAuditorId(auditorId);
        update.setAuditorName(auditorName);
        update.setUpdateBy(updateBy);
        spaceReservationMapper.updateReservationStatus(update);
    }

    private void writeAuditLog(Long reservationId, Long itemId, String action, String beforeStatus, String afterStatus, Long auditorId, String auditorName, String opinion, String createBy)
    {
        SpaceAuditLog log = new SpaceAuditLog();
        log.setReservationId(reservationId);
        log.setItemId(itemId);
        log.setAuditAction(action);
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(afterStatus);
        log.setAuditorId(auditorId);
        log.setAuditorName(auditorName);
        log.setAuditOpinion(opinion);
        log.setAuditTime(new Date());
        log.setCreateBy(createBy);
        spaceAuditLogMapper.insertSpaceAuditLog(log);
    }
}
