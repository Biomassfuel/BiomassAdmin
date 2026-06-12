package com.ruoyi.system.service.space.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.space.SpaceAuditLog;
import com.ruoyi.system.domain.space.SpaceBlackout;
import com.ruoyi.system.domain.space.SpaceReservation;
import com.ruoyi.system.domain.space.SpaceReservationItem;
import com.ruoyi.system.domain.space.SpaceReservationRule;
import com.ruoyi.system.domain.space.SpaceRoom;
import com.ruoyi.system.domain.space.SpaceTimePeriod;
import com.ruoyi.system.mapper.space.SpaceAuditLogMapper;
import com.ruoyi.system.mapper.space.SpaceBlackoutMapper;
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

    private static final String STATUS_PENDING = "1";

    private static final String STATUS_APPROVED = "2";

    private static final String STATUS_PARTIAL = "3";

    private static final String STATUS_REJECTED = "4";

    private static final String STATUS_CANCELED = "5";

    private static final String STATUS_FINISHED = "6";

    private static final String ITEM_STATUS_PENDING = "1";

    private static final String ITEM_STATUS_APPROVED = "2";

    private static final String ITEM_STATUS_REJECTED = "3";

    private static final String ITEM_STATUS_CONFLICT = "4";

    private static final String ITEM_STATUS_CANCELED = "5";

    private static final String ITEM_STATUS_FINISHED = "6";

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
    private SpaceBlackoutMapper spaceBlackoutMapper;

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

    private SpaceReservation selectSpaceReservationByIdForUpdate(Long reservationId)
    {
        SpaceReservation reservation = spaceReservationMapper.selectSpaceReservationByIdForUpdate(reservationId);
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
            spaceReservation.setReservationNo("YY" + IdUtils.fastSimpleUUID());
        }
        if (StringUtils.isEmpty(spaceReservation.getReservationType()))
        {
            spaceReservation.setReservationType("0");
        }

        List<SpaceReservationItem> validationItems = orderedItems(items);
        for (SpaceReservationItem item : validationItems)
        {
            fillRoomSnapshot(item);
            assertNoConflict(item, spaceReservation.getCreateBy());
        }
        assertNoInternalConflicts(validationItems);

        spaceReservation.setStatus(STATUS_PENDING);
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
            item.setItemStatus(ITEM_STATUS_PENDING);
            item.setConflictFlag("0");
            item.setConflictReason("");
            item.setConflictItemId(null);
            spaceReservationItemMapper.insertSpaceReservationItem(item);
        }
        writeAuditLog(spaceReservation.getReservationId(), null, "0", "", STATUS_PENDING, spaceReservation.getApplicantId(), spaceReservation.getApplicantName(), "提交预约申请", spaceReservation.getCreateBy());
        return rows;
    }

    @Override
    public int updateSpaceReservation(SpaceReservation spaceReservation)
    {
        return spaceReservationMapper.updateSpaceReservation(spaceReservation);
    }

    @Override
    @Transactional
    public int deleteSpaceReservationByIds(Long[] reservationIds)
    {
        assertCanDeleteReservations(reservationIds);
        return spaceReservationMapper.deleteSpaceReservationByIds(reservationIds);
    }

    @Override
    @Transactional
    public int deleteSpaceReservationById(Long reservationId)
    {
        assertCanDeleteReservations(new Long[] { reservationId });
        return spaceReservationMapper.deleteSpaceReservationById(reservationId);
    }

    @Override
    @Transactional
    public int insertSpaceReservationItem(SpaceReservationItem spaceReservationItem)
    {
        if (spaceReservationItem == null || spaceReservationItem.getReservationId() == null)
        {
            throw new ServiceException("预约场次必须关联主预约");
        }
        SpaceReservation reservation = selectSpaceReservationByIdForUpdate(spaceReservationItem.getReservationId());
        assertReservationOpenForItemWrite(reservation);
        fillRoomSnapshot(spaceReservationItem);
        assertNoConflict(spaceReservationItem, spaceReservationItem.getCreateBy());

        spaceReservationItem.setReservationId(reservation.getReservationId());
        spaceReservationItem.setItemStatus(ITEM_STATUS_PENDING);
        spaceReservationItem.setConflictFlag("0");
        spaceReservationItem.setConflictReason("");
        spaceReservationItem.setConflictItemId(null);
        spaceReservationItem.setAuditorId(null);
        spaceReservationItem.setAuditorName("");
        spaceReservationItem.setRejectReason("");
        int rows = spaceReservationItemMapper.insertSpaceReservationItem(spaceReservationItem);
        refreshReservationStatus(reservation.getReservationId(), null, "", spaceReservationItem.getCreateBy());
        return rows;
    }

    @Override
    @Transactional
    public int updateSpaceReservationItem(SpaceReservationItem spaceReservationItem)
    {
        if (spaceReservationItem == null || spaceReservationItem.getItemId() == null)
        {
            throw new ServiceException("预约场次不存在");
        }
        SpaceReservationItem current = spaceReservationItemMapper.selectSpaceReservationItemById(spaceReservationItem.getItemId());
        if (current == null)
        {
            throw new ServiceException("预约场次不存在");
        }
        SpaceReservation reservation = selectSpaceReservationByIdForUpdate(current.getReservationId());
        current = spaceReservationItemMapper.selectSpaceReservationItemByIdForUpdate(spaceReservationItem.getItemId());
        if (current == null || !reservation.getReservationId().equals(current.getReservationId()))
        {
            throw new ServiceException("状态已变化，请刷新后重试");
        }
        assertItemDirectEditable(current);
        assertReservationOpenForItemWrite(reservation);

        SpaceReservationItem update = mergeDirectItemUpdate(current, spaceReservationItem);
        fillRoomSnapshot(update);
        assertNoConflict(update, spaceReservationItem.getUpdateBy());
        int rows = spaceReservationItemMapper.updateSpaceReservationItem(update);

        SpaceReservationItem statusUpdate = new SpaceReservationItem();
        statusUpdate.setItemId(current.getItemId());
        statusUpdate.setItemStatus(ITEM_STATUS_PENDING);
        statusUpdate.setConflictFlag("0");
        statusUpdate.setConflictReason("");
        statusUpdate.setConflictItemId(null);
        statusUpdate.setAuditorId(null);
        statusUpdate.setAuditorName("");
        statusUpdate.setRejectReason("");
        statusUpdate.setUpdateBy(spaceReservationItem.getUpdateBy());
        rows += updateItemStatusIfCurrent(statusUpdate, current.getItemStatus());
        refreshReservationStatus(reservation.getReservationId(), null, "", spaceReservationItem.getUpdateBy());
        return rows;
    }

    @Override
    @Transactional
    public int deleteSpaceReservationItemByIds(Long[] itemIds, String updateBy)
    {
        if (itemIds == null || itemIds.length == 0)
        {
            return 0;
        }
        List<SpaceReservationItem> itemsToDelete = new ArrayList<>();
        for (Long itemId : itemIds)
        {
            SpaceReservationItem item = spaceReservationItemMapper.selectSpaceReservationItemById(itemId);
            if (item == null)
            {
                throw new ServiceException("预约场次不存在");
            }
            itemsToDelete.add(item);
        }
        itemsToDelete.sort(Comparator
                .comparing(SpaceReservationItem::getReservationId, Comparator.nullsLast(Long::compareTo))
                .thenComparing(SpaceReservationItem::getItemId, Comparator.nullsLast(Long::compareTo)));

        int rows = 0;
        for (SpaceReservationItem itemToDelete : itemsToDelete)
        {
            Long itemId = itemToDelete.getItemId();
            SpaceReservationItem item = itemToDelete;
            SpaceReservation reservation = selectSpaceReservationByIdForUpdate(item.getReservationId());
            item = spaceReservationItemMapper.selectSpaceReservationItemByIdForUpdate(itemId);
            if (item == null || !reservation.getReservationId().equals(item.getReservationId()))
            {
                throw new ServiceException("状态已变化，请刷新后重试");
            }
            assertItemDirectDeletable(item);
            assertReservationOpenForItemWrite(reservation);
            rows += spaceReservationItemMapper.deleteSpaceReservationItemById(itemId);
            if (spaceReservationItemMapper.countItemsByReservationId(item.getReservationId()) == 0)
            {
                SpaceReservation update = new SpaceReservation();
                update.setReservationId(item.getReservationId());
                update.setStatus(STATUS_CANCELED);
                update.setAuditType(AUDIT_TYPE_NORMAL);
                update.setUpdateBy(updateBy);
                updateReservationStatusIfCurrent(update, reservation.getStatus(), reservation.getAuditType());
            }
            else
            {
                refreshReservationStatus(item.getReservationId(), null, "", updateBy);
            }
        }
        return rows;
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
        SpaceReservation reservation = selectSpaceReservationByIdForUpdate(reservationId);
        if (reservation == null)
        {
            throw new ServiceException("预约申请不存在");
        }
        assertReservationAuditOpen(reservation);
        boolean hasAuditableItem = false;
        for (SpaceReservationItem item : orderedItems(reservation.getItems()))
        {
            if (!isAuditableItem(item))
            {
                continue;
            }
            if ("1".equals(item.getConflictFlag()) || ITEM_STATUS_CONFLICT.equals(item.getItemStatus()))
            {
                throw new ServiceException("存在冲突场次，请在冲突预约处理或详情中逐项处理");
            }
            hasAuditableItem = true;
            assertNoConflict(item, updateBy);
            String before = item.getItemStatus();
            item.setItemStatus(ITEM_STATUS_APPROVED);
            item.setConflictFlag("0");
            item.setConflictReason("");
            item.setAuditorId(auditorId);
            item.setAuditorName(auditorName);
            item.setRejectReason("");
            item.setUpdateBy(updateBy);
            updateItemStatusIfCurrent(item, before);
        }
        if (!hasAuditableItem)
        {
            throw new ServiceException("当前预约没有待审核场次");
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus(resolveFinishedStatus(reservation.getItems()));
        update.setAuditType(AUDIT_TYPE_NORMAL);
        update.setAuditorId(auditorId);
        update.setAuditorName(auditorName);
        update.setRejectReason("");
        update.setUpdateBy(updateBy);
        int rows = updateReservationStatusIfCurrent(update, reservation.getStatus(), reservation.getAuditType());
        writeAuditLog(reservationId, null, "1", reservation.getStatus(), update.getStatus(), auditorId, auditorName, opinion, updateBy);
        return rows;
    }

    @Override
    @Transactional
    public int rejectReservation(Long reservationId, Long auditorId, String auditorName, String reason, String updateBy)
    {
        SpaceReservation reservation = selectSpaceReservationByIdForUpdate(reservationId);
        if (reservation == null)
        {
            throw new ServiceException("预约申请不存在");
        }
        assertReservationAuditOpen(reservation);
        boolean hasAuditableItem = false;
        for (SpaceReservationItem item : orderedItems(reservation.getItems()))
        {
            if (!isAuditableItem(item))
            {
                continue;
            }
            hasAuditableItem = true;
            String before = item.getItemStatus();
            item.setItemStatus(ITEM_STATUS_REJECTED);
            item.setConflictFlag("0");
            item.setConflictReason("");
            item.setAuditorId(auditorId);
            item.setAuditorName(auditorName);
            item.setRejectReason(reason);
            item.setUpdateBy(updateBy);
            updateItemStatusIfCurrent(item, before);
        }
        if (!hasAuditableItem)
        {
            throw new ServiceException("当前预约没有待审核场次");
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus(resolveFinishedStatus(reservation.getItems()));
        update.setAuditType(AUDIT_TYPE_NORMAL);
        update.setAuditorId(auditorId);
        update.setAuditorName(auditorName);
        update.setRejectReason(reason);
        update.setUpdateBy(updateBy);
        int rows = updateReservationStatusIfCurrent(update, reservation.getStatus(), reservation.getAuditType());
        writeAuditLog(reservationId, null, "2", reservation.getStatus(), update.getStatus(), auditorId, auditorName, reason, updateBy);
        return rows;
    }

    @Override
    @Transactional
    public int cancelReservation(Long reservationId, String updateBy)
    {
        SpaceReservation reservation = selectSpaceReservationByIdForUpdate(reservationId);
        if (reservation == null)
        {
            throw new ServiceException("预约申请不存在");
        }
        if (AUDIT_TYPE_CANCEL.equals(reservation.getAuditType()))
        {
            throw new ServiceException("取消申请正在审核中");
        }
        if (STATUS_APPROVED.equals(reservation.getStatus()) || STATUS_PARTIAL.equals(reservation.getStatus()))
        {
            return submitCancelAudit(reservation, updateBy);
        }
        if (!STATUS_PENDING.equals(reservation.getStatus()))
        {
            throw new ServiceException("当前状态不支持取消");
        }
        for (SpaceReservationItem item : reservation.getItems())
        {
            String before = item.getItemStatus();
            item.setItemStatus(ITEM_STATUS_CANCELED);
            item.setConflictFlag("0");
            item.setConflictReason("");
            item.setUpdateBy(updateBy);
            updateItemStatusIfCurrent(item, before);
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus(STATUS_CANCELED);
        update.setAuditType(AUDIT_TYPE_NORMAL);
        update.setUpdateBy(updateBy);
        int rows = updateReservationStatusIfCurrent(update, reservation.getStatus(), reservation.getAuditType());
        writeAuditLog(reservationId, null, "4", reservation.getStatus(), STATUS_CANCELED, null, updateBy, "取消预约申请", updateBy);
        return rows;
    }

    @Override
    @Transactional
    public int approveCancelReservation(Long reservationId, Long auditorId, String auditorName, String opinion, String updateBy)
    {
        SpaceReservation reservation = selectCancelAuditReservation(reservationId);
        for (SpaceReservationItem item : reservation.getItems())
        {
            if (ITEM_STATUS_PENDING.equals(item.getItemStatus()))
            {
                String before = item.getItemStatus();
                item.setItemStatus(ITEM_STATUS_CANCELED);
                item.setConflictFlag("0");
                item.setConflictReason("");
                item.setAuditorId(auditorId);
                item.setAuditorName(auditorName);
                item.setRejectReason("");
                item.setUpdateBy(updateBy);
                updateItemStatusIfCurrent(item, before);
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
        int rows = updateReservationStatusIfCurrent(update, reservation.getStatus(), reservation.getAuditType());
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
            if (ITEM_STATUS_PENDING.equals(item.getItemStatus()))
            {
                String before = item.getItemStatus();
                item.setItemStatus(ITEM_STATUS_APPROVED);
                item.setConflictFlag("0");
                item.setConflictReason("");
                item.setAuditorId(auditorId);
                item.setAuditorName(auditorName);
                item.setRejectReason(reason);
                item.setUpdateBy(updateBy);
                updateItemStatusIfCurrent(item, before);
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
        int rows = updateReservationStatusIfCurrent(update, reservation.getStatus(), reservation.getAuditType());
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
        SpaceReservation reservation = selectSpaceReservationByIdForUpdate(item.getReservationId());
        item = spaceReservationItemMapper.selectSpaceReservationItemByIdForUpdate(itemId);
        if (item == null || !reservation.getReservationId().equals(item.getReservationId()))
        {
            throw new ServiceException("状态已变化，请刷新后重试");
        }
        assertReservationAllowsItemAudit(reservation);
        assertItemAuditable(item);
        assertNoConflict(item, updateBy);
        String before = item.getItemStatus();
        item.setItemStatus(ITEM_STATUS_APPROVED);
        item.setConflictFlag("0");
        item.setConflictReason("");
        item.setAuditorId(auditorId);
        item.setAuditorName(auditorName);
        item.setRejectReason("");
        item.setUpdateBy(updateBy);
        int rows = updateItemStatusIfCurrent(item, before);
        writeAuditLog(item.getReservationId(), itemId, "5", before, ITEM_STATUS_APPROVED, auditorId, auditorName, opinion, updateBy);
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
        SpaceReservation reservation = selectSpaceReservationByIdForUpdate(item.getReservationId());
        item = spaceReservationItemMapper.selectSpaceReservationItemByIdForUpdate(itemId);
        if (item == null || !reservation.getReservationId().equals(item.getReservationId()))
        {
            throw new ServiceException("状态已变化，请刷新后重试");
        }
        assertReservationAllowsItemAudit(reservation);
        assertItemAuditable(item);
        String before = item.getItemStatus();
        item.setItemStatus(ITEM_STATUS_REJECTED);
        item.setConflictFlag("0");
        item.setConflictReason("");
        item.setAuditorId(auditorId);
        item.setAuditorName(auditorName);
        item.setRejectReason(reason);
        item.setUpdateBy(updateBy);
        int rows = updateItemStatusIfCurrent(item, before);
        writeAuditLog(item.getReservationId(), itemId, "6", before, ITEM_STATUS_REJECTED, auditorId, auditorName, reason, updateBy);
        refreshReservationStatus(item.getReservationId(), auditorId, auditorName, updateBy);
        return rows;
    }

    @Override
    @Transactional
    public int approveCancelItem(Long itemId, Long auditorId, String auditorName, String opinion, String updateBy)
    {
        SpaceReservationItem item = selectCancelAuditItem(itemId);
        String before = item.getItemStatus();
        item.setItemStatus(ITEM_STATUS_CANCELED);
        item.setConflictFlag("0");
        item.setConflictReason("");
        item.setAuditorId(auditorId);
        item.setAuditorName(auditorName);
        item.setRejectReason("");
        item.setUpdateBy(updateBy);
        int rows = updateItemStatusIfCurrent(item, before);
        writeAuditLog(item.getReservationId(), itemId, "A", before, ITEM_STATUS_CANCELED, auditorId, auditorName, opinion, updateBy);
        refreshCancelAuditReservationStatus(item.getReservationId(), auditorId, auditorName, updateBy, "");
        return rows;
    }

    @Override
    @Transactional
    public int rejectCancelItem(Long itemId, Long auditorId, String auditorName, String reason, String updateBy)
    {
        SpaceReservationItem item = selectCancelAuditItem(itemId);
        String before = item.getItemStatus();
        item.setItemStatus(ITEM_STATUS_APPROVED);
        item.setConflictFlag("0");
        item.setConflictReason("");
        item.setAuditorId(auditorId);
        item.setAuditorName(auditorName);
        item.setRejectReason(reason);
        item.setUpdateBy(updateBy);
        int rows = updateItemStatusIfCurrent(item, before);
        writeAuditLog(item.getReservationId(), itemId, "B", before, ITEM_STATUS_APPROVED, auditorId, auditorName, reason, updateBy);
        refreshCancelAuditReservationStatus(item.getReservationId(), auditorId, auditorName, updateBy, reason);
        return rows;
    }

    private int submitCancelAudit(SpaceReservation reservation, String updateBy)
    {
        boolean hasApprovedItem = false;
        for (SpaceReservationItem item : reservation.getItems())
        {
            if (ITEM_STATUS_APPROVED.equals(item.getItemStatus()))
            {
                hasApprovedItem = true;
                String before = item.getItemStatus();
                item.setItemStatus(ITEM_STATUS_PENDING);
                item.setConflictFlag("0");
                item.setConflictReason("");
                item.setAuditorId(null);
                item.setAuditorName("");
                item.setRejectReason("");
                item.setUpdateBy(updateBy);
                updateItemStatusIfCurrent(item, before);
            }
        }
        if (!hasApprovedItem)
        {
            throw new ServiceException("当前预约没有可取消的已通过场次");
        }
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservation.getReservationId());
        update.setStatus(STATUS_PENDING);
        update.setAuditType(AUDIT_TYPE_CANCEL);
        update.setAuditorId(null);
        update.setAuditorName("");
        update.setRejectReason("");
        update.setUpdateBy(updateBy);
        int rows = updateReservationStatusIfCurrent(update, reservation.getStatus(), reservation.getAuditType());
        writeAuditLog(reservation.getReservationId(), null, "7", reservation.getStatus(), STATUS_PENDING, null, updateBy, "发起取消审核", updateBy);
        return rows;
    }

    private SpaceReservation selectCancelAuditReservation(Long reservationId)
    {
        SpaceReservation reservation = selectSpaceReservationByIdForUpdate(reservationId);
        if (reservation == null)
        {
            throw new ServiceException("预约申请不存在");
        }
        if (!STATUS_PENDING.equals(reservation.getStatus()) || !AUDIT_TYPE_CANCEL.equals(reservation.getAuditType()))
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
        item = spaceReservationItemMapper.selectSpaceReservationItemByIdForUpdate(itemId);
        if (item == null || !reservation.getReservationId().equals(item.getReservationId()))
        {
            throw new ServiceException("状态已变化，请刷新后重试");
        }
        if (!ITEM_STATUS_PENDING.equals(item.getItemStatus()))
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
        SpaceReservation reservation = selectSpaceReservationByIdForUpdate(reservationId);
        if (reservation == null || !STATUS_PENDING.equals(reservation.getStatus()) || !AUDIT_TYPE_CANCEL.equals(reservation.getAuditType()))
        {
            throw new ServiceException("状态已变化，请刷新后重试");
        }
        SpaceReservationItem query = new SpaceReservationItem();
        query.setReservationId(reservationId);
        List<SpaceReservationItem> items = spaceReservationItemMapper.selectSpaceReservationItemList(query);
        for (SpaceReservationItem item : items)
        {
            if (ITEM_STATUS_PENDING.equals(item.getItemStatus()))
            {
                SpaceReservation update = new SpaceReservation();
                update.setReservationId(reservationId);
                update.setStatus(STATUS_PENDING);
                update.setAuditType(AUDIT_TYPE_CANCEL);
                update.setAuditorId(auditorId);
                update.setAuditorName(auditorName);
                update.setRejectReason(rejectReason);
                update.setUpdateBy(updateBy);
                updateReservationStatusIfCurrent(update, reservation.getStatus(), reservation.getAuditType());
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
        updateReservationStatusIfCurrent(update, reservation.getStatus(), reservation.getAuditType());
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
        SpaceReservation reservation = selectSpaceReservationByIdForUpdate(reservationId);
        if (reservation == null || AUDIT_TYPE_CANCEL.equals(reservation.getAuditType())
                || !(STATUS_APPROVED.equals(reservation.getStatus()) || STATUS_PARTIAL.equals(reservation.getStatus())))
        {
            return;
        }
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
        update.setAuditType(reservation.getAuditType());
        update.setAuditorId(reservation.getAuditorId());
        update.setAuditorName(reservation.getAuditorName());
        update.setRejectReason(reservation.getRejectReason());
        update.setUpdateBy("system");
        spaceReservationMapper.updateReservationStatusIfCurrent(update, reservation.getStatus(), reservation.getAuditType());
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
        item.setStartTime(normalizeBookingTime(item.getStartTime()));
        item.setEndTime(normalizeBookingTime(item.getEndTime()));
        spaceRoomDayLockMapper.insertIgnore(item.getRoomId(), item.getBookingDate(), operator);
        spaceRoomDayLockMapper.lockRoomDay(item.getRoomId(), item.getBookingDate());
        SpaceReservationItem conflict = selectFirstConflict(item);
        if (conflict != null)
        {
            throw new ServiceException("预约时间冲突：" + buildConflictReason(conflict));
        }
        SpaceBlackout blackout = selectFirstBlackoutConflict(item);
        if (blackout != null)
        {
            throw new ServiceException("预约时间处于维护停用时段：" + buildBlackoutReason(blackout));
        }
    }

    private SpaceReservationItem selectFirstConflict(SpaceReservationItem item)
    {
        List<SpaceReservationItem> conflicts = spaceReservationItemMapper.selectConflictItems(item);
        return conflicts == null || conflicts.isEmpty() ? null : conflicts.get(0);
    }

    private List<SpaceReservationItem> orderedItems(List<SpaceReservationItem> items)
    {
        List<SpaceReservationItem> ordered = new ArrayList<>();
        if (items != null)
        {
            ordered.addAll(items);
        }
        ordered.sort(Comparator
                .comparing(SpaceReservationItem::getRoomId, Comparator.nullsLast(Long::compareTo))
                .thenComparing(SpaceReservationItem::getBookingDate, Comparator.nullsLast(String::compareTo))
                .thenComparing(SpaceReservationItem::getStartTime, Comparator.nullsLast(String::compareTo))
                .thenComparing(SpaceReservationItem::getEndTime, Comparator.nullsLast(String::compareTo)));
        return ordered;
    }

    private void assertNoInternalConflicts(List<SpaceReservationItem> items)
    {
        if (items == null)
        {
            return;
        }
        for (int i = 0; i < items.size(); i++)
        {
            SpaceReservationItem left = items.get(i);
            for (int j = i + 1; j < items.size(); j++)
            {
                SpaceReservationItem right = items.get(j);
                if (left.getRoomId() != null && left.getRoomId().equals(right.getRoomId())
                        && left.getBookingDate() != null && left.getBookingDate().equals(right.getBookingDate())
                        && left.getStartTime().compareTo(right.getEndTime()) < 0
                        && left.getEndTime().compareTo(right.getStartTime()) > 0)
                {
                    throw new ServiceException("同一次预约内存在重复或重叠场次：" + buildConflictReason(left));
                }
            }
        }
    }

    private SpaceBlackout selectFirstBlackoutConflict(SpaceReservationItem item)
    {
        List<SpaceBlackout> conflicts = spaceBlackoutMapper.selectConflictBlackouts(item);
        return conflicts == null || conflicts.isEmpty() ? null : conflicts.get(0);
    }

    private String buildConflictReason(SpaceReservationItem conflict)
    {
        return conflict.getRoomCode() + " " + conflict.getBookingDate() + " " + conflict.getStartTime() + "-" + conflict.getEndTime();
    }

    private String buildBlackoutReason(SpaceBlackout blackout)
    {
        String reason = StringUtils.isEmpty(blackout.getReason()) ? "" : " " + blackout.getReason();
        return blackout.getRoomCode() + " " + blackout.getStartTime() + "-" + blackout.getEndTime() + reason;
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
        return normalizeBookingTime(standardTime).equals(normalizeBookingTime(inputTime));
    }

    private String normalizeTime(String time)
    {
        return time.length() >= 8 ? time.substring(0, 8) : time;
    }

    private void refreshReservationStatus(Long reservationId, Long auditorId, String auditorName, String updateBy)
    {
        SpaceReservation reservation = selectSpaceReservationByIdForUpdate(reservationId);
        if (reservation == null || !AUDIT_TYPE_NORMAL.equals(reservation.getAuditType())
                || STATUS_CANCELED.equals(reservation.getStatus()) || STATUS_FINISHED.equals(reservation.getStatus()))
        {
            throw new ServiceException("状态已变化，请刷新后重试");
        }
        SpaceReservationItem query = new SpaceReservationItem();
        query.setReservationId(reservationId);
        List<SpaceReservationItem> items = spaceReservationItemMapper.selectSpaceReservationItemList(query);
        SpaceReservation update = new SpaceReservation();
        update.setReservationId(reservationId);
        update.setStatus(resolveFinishedStatus(items));
        update.setAuditType(AUDIT_TYPE_NORMAL);
        update.setAuditorId(auditorId == null ? reservation.getAuditorId() : auditorId);
        update.setAuditorName(auditorName == null ? reservation.getAuditorName() : auditorName);
        update.setRejectReason(reservation.getRejectReason());
        update.setUpdateBy(updateBy);
        updateReservationStatusIfCurrent(update, reservation.getStatus(), reservation.getAuditType());
    }

    private void assertReservationAllowsItemAudit(SpaceReservation reservation)
    {
        if (reservation == null || !AUDIT_TYPE_NORMAL.equals(reservation.getAuditType())
                || !(STATUS_PENDING.equals(reservation.getStatus()) || STATUS_PARTIAL.equals(reservation.getStatus())))
        {
            throw new ServiceException("状态已变化，请刷新后重试");
        }
    }

    private void assertReservationAuditOpen(SpaceReservation reservation)
    {
        if (reservation == null || !AUDIT_TYPE_NORMAL.equals(reservation.getAuditType())
                || !(STATUS_PENDING.equals(reservation.getStatus()) || STATUS_PARTIAL.equals(reservation.getStatus())))
        {
            throw new ServiceException("状态已变化，请刷新后重试");
        }
    }

    private boolean isAuditableItem(SpaceReservationItem item)
    {
        return item != null && (ITEM_STATUS_PENDING.equals(item.getItemStatus()) || ITEM_STATUS_CONFLICT.equals(item.getItemStatus()));
    }

    private void assertReservationOpenForItemWrite(SpaceReservation reservation)
    {
        if (reservation == null || !"0".equals(reservation.getDelFlag()))
        {
            throw new ServiceException("预约申请不存在或已删除");
        }
        if (!AUDIT_TYPE_NORMAL.equals(reservation.getAuditType()))
        {
            throw new ServiceException("取消审核中的预约不能直接维护场次");
        }
        if (STATUS_CANCELED.equals(reservation.getStatus()) || STATUS_FINISHED.equals(reservation.getStatus()))
        {
            throw new ServiceException("终态预约不能直接维护场次");
        }
    }

    private void assertItemDirectEditable(SpaceReservationItem item)
    {
        if (item == null || !(ITEM_STATUS_PENDING.equals(item.getItemStatus()) || ITEM_STATUS_REJECTED.equals(item.getItemStatus())
                || ITEM_STATUS_CONFLICT.equals(item.getItemStatus())))
        {
            throw new ServiceException("当前场次状态不允许直接编辑");
        }
    }

    private void assertItemDirectDeletable(SpaceReservationItem item)
    {
        if (item == null || ITEM_STATUS_APPROVED.equals(item.getItemStatus()) || ITEM_STATUS_FINISHED.equals(item.getItemStatus())
                || ITEM_STATUS_CANCELED.equals(item.getItemStatus()))
        {
            throw new ServiceException("当前场次状态不允许直接删除");
        }
    }

    private SpaceReservationItem mergeDirectItemUpdate(SpaceReservationItem current, SpaceReservationItem input)
    {
        SpaceReservationItem update = new SpaceReservationItem();
        update.setItemId(current.getItemId());
        update.setReservationId(current.getReservationId());
        update.setRoomId(input.getRoomId() == null ? current.getRoomId() : input.getRoomId());
        update.setBookingDate(StringUtils.isEmpty(input.getBookingDate()) ? current.getBookingDate() : input.getBookingDate());
        update.setWeekday(StringUtils.isEmpty(input.getWeekday()) ? current.getWeekday() : input.getWeekday());
        update.setStartTime(StringUtils.isEmpty(input.getStartTime()) ? current.getStartTime() : input.getStartTime());
        update.setEndTime(StringUtils.isEmpty(input.getEndTime()) ? current.getEndTime() : input.getEndTime());
        update.setRemark(input.getRemark());
        update.setUpdateBy(input.getUpdateBy());
        return update;
    }

    private void assertReservationStatus(SpaceReservation reservation, String expectedStatus, String expectedAuditType)
    {
        if (reservation == null || !expectedStatus.equals(reservation.getStatus()) || !expectedAuditType.equals(reservation.getAuditType()))
        {
            throw new ServiceException("状态已变化，请刷新后重试");
        }
    }

    private void assertItemAuditable(SpaceReservationItem item)
    {
        if (item == null || !(ITEM_STATUS_PENDING.equals(item.getItemStatus()) || ITEM_STATUS_CONFLICT.equals(item.getItemStatus())))
        {
            throw new ServiceException("状态已变化，请刷新后重试");
        }
    }

    private int updateReservationStatusIfCurrent(SpaceReservation update, String expectedStatus, String expectedAuditType)
    {
        int rows = spaceReservationMapper.updateReservationStatusIfCurrent(update, expectedStatus, expectedAuditType);
        if (rows == 0)
        {
            throw new ServiceException("状态已变化，请刷新后重试");
        }
        return rows;
    }

    private int updateItemStatusIfCurrent(SpaceReservationItem item, String expectedStatus)
    {
        int rows = spaceReservationItemMapper.updateItemStatusIfCurrent(item, expectedStatus);
        if (rows == 0)
        {
            throw new ServiceException("状态已变化，请刷新后重试");
        }
        return rows;
    }

    private void assertCanDeleteReservations(Long[] reservationIds)
    {
        if (reservationIds == null || reservationIds.length == 0)
        {
            return;
        }
        Long[] orderedReservationIds = Arrays.stream(reservationIds)
                .filter(id -> id != null)
                .sorted()
                .toArray(Long[]::new);
        for (Long reservationId : orderedReservationIds)
        {
            spaceReservationMapper.selectSpaceReservationByIdForUpdate(reservationId);
        }
        List<SpaceReservationItem> blockingItems = spaceReservationItemMapper.selectBlockingItemsByReservationIds(reservationIds);
        if (blockingItems != null && !blockingItems.isEmpty())
        {
            SpaceReservationItem item = blockingItems.get(0);
            throw new ServiceException("预约存在未结束或待审核场次，不能删除：" + buildConflictReason(item));
        }
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
