package com.ruoyi.system.service.space.impl;

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
    public int approveReservation(Long reservationId, Long auditorId, String auditorName, String opinion, String updateBy)
    {
        SpaceReservation reservation = selectSpaceReservationById(reservationId);
        if (reservation == null)
        {
            throw new ServiceException("预约申请不存在");
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
        update.setUpdateBy(updateBy);
        int rows = spaceReservationMapper.updateReservationStatus(update);
        writeAuditLog(reservationId, null, "4", reservation.getStatus(), "5", null, updateBy, "取消预约申请", updateBy);
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

    private void fillRoomSnapshot(SpaceReservationItem item)
    {
        SpaceRoom room = spaceRoomMapper.selectSpaceRoomById(item.getRoomId());
        if (room == null)
        {
            throw new ServiceException("房间不存在");
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
        int approved = 0;
        int rejected = 0;
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
            else if ("1".equals(item.getItemStatus()) || "4".equals(item.getItemStatus()))
            {
                pending++;
            }
        }
        String status = "3";
        if (approved == items.size())
        {
            status = "2";
        }
        else if (rejected == items.size())
        {
            status = "4";
        }
        else if (approved == 0 && rejected == 0 && pending > 0)
        {
            status = "1";
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus(status);
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
