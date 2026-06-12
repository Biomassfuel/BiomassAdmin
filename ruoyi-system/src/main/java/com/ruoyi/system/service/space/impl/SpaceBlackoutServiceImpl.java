package com.ruoyi.system.service.space.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.space.SpaceBlackout;
import com.ruoyi.system.domain.space.SpaceReservationItem;
import com.ruoyi.system.domain.space.SpaceRoom;
import com.ruoyi.system.mapper.space.SpaceBlackoutMapper;
import com.ruoyi.system.mapper.space.SpaceRoomDayLockMapper;
import com.ruoyi.system.mapper.space.SpaceRoomMapper;
import com.ruoyi.system.service.space.ISpaceBlackoutService;

@Service
public class SpaceBlackoutServiceImpl implements ISpaceBlackoutService
{
    private static final String STATUS_ENABLED = "0";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private SpaceBlackoutMapper spaceBlackoutMapper;

    @Autowired
    private SpaceRoomMapper spaceRoomMapper;

    @Autowired
    private SpaceRoomDayLockMapper spaceRoomDayLockMapper;

    @Override
    public SpaceBlackout selectSpaceBlackoutById(Long blackoutId)
    {
        return spaceBlackoutMapper.selectSpaceBlackoutById(blackoutId);
    }

    @Override
    public List<SpaceBlackout> selectSpaceBlackoutList(SpaceBlackout spaceBlackout)
    {
        return spaceBlackoutMapper.selectSpaceBlackoutList(spaceBlackout);
    }

    @Override
    @Transactional
    public int insertSpaceBlackout(SpaceBlackout spaceBlackout)
    {
        fillRoomSnapshot(spaceBlackout);
        if (StringUtils.isEmpty(spaceBlackout.getStatus()))
        {
            spaceBlackout.setStatus(STATUS_ENABLED);
        }
        if (STATUS_ENABLED.equals(spaceBlackout.getStatus()))
        {
            assertNoReservationConflict(spaceBlackout, spaceBlackout.getCreateBy());
        }
        return spaceBlackoutMapper.insertSpaceBlackout(spaceBlackout);
    }

    @Override
    @Transactional
    public int updateSpaceBlackout(SpaceBlackout spaceBlackout)
    {
        SpaceBlackout current = spaceBlackoutMapper.selectSpaceBlackoutById(spaceBlackout.getBlackoutId());
        if (current == null)
        {
            throw new ServiceException("维护停用记录不存在");
        }
        SpaceBlackout merged = mergeBlackout(current, spaceBlackout);
        fillRoomSnapshot(merged);
        if (STATUS_ENABLED.equals(merged.getStatus()))
        {
            assertNoReservationConflict(merged, spaceBlackout.getUpdateBy());
        }
        merged.setUpdateBy(spaceBlackout.getUpdateBy());
        merged.setRemark(spaceBlackout.getRemark());
        return spaceBlackoutMapper.updateSpaceBlackout(merged);
    }

    @Override
    public int deleteSpaceBlackoutByIds(Long[] blackoutIds)
    {
        return spaceBlackoutMapper.deleteSpaceBlackoutByIds(blackoutIds);
    }

    @Override
    public int deleteSpaceBlackoutById(Long blackoutId)
    {
        return spaceBlackoutMapper.deleteSpaceBlackoutById(blackoutId);
    }

    private SpaceBlackout mergeBlackout(SpaceBlackout current, SpaceBlackout input)
    {
        SpaceBlackout merged = new SpaceBlackout();
        merged.setBlackoutId(current.getBlackoutId());
        merged.setRoomId(input.getRoomId() == null ? current.getRoomId() : input.getRoomId());
        merged.setRoomCode(StringUtils.isEmpty(input.getRoomCode()) ? current.getRoomCode() : input.getRoomCode());
        merged.setStartTime(StringUtils.isEmpty(input.getStartTime()) ? current.getStartTime() : input.getStartTime());
        merged.setEndTime(StringUtils.isEmpty(input.getEndTime()) ? current.getEndTime() : input.getEndTime());
        merged.setReason(StringUtils.isEmpty(input.getReason()) ? current.getReason() : input.getReason());
        merged.setStatus(StringUtils.isEmpty(input.getStatus()) ? current.getStatus() : input.getStatus());
        return merged;
    }

    private void fillRoomSnapshot(SpaceBlackout blackout)
    {
        if (blackout == null || blackout.getRoomId() == null)
        {
            throw new ServiceException("维护停用房间不能为空");
        }
        SpaceRoom room = spaceRoomMapper.selectSpaceRoomById(blackout.getRoomId());
        if (room == null || !"0".equals(room.getDelFlag()))
        {
            throw new ServiceException("维护停用房间不存在或已删除");
        }
        blackout.setRoomCode(room.getRoomCode());
    }

    private void assertNoReservationConflict(SpaceBlackout blackout, String operator)
    {
        LocalDateTime start = parseDateTime(blackout.getStartTime(), "维护开始时间格式不正确");
        LocalDateTime end = parseDateTime(blackout.getEndTime(), "维护结束时间格式不正确");
        if (!start.isBefore(end))
        {
            throw new ServiceException("维护结束时间必须晚于开始时间");
        }
        lockBlackoutDays(blackout.getRoomId(), start.toLocalDate(), end.toLocalDate(), operator);
        List<SpaceReservationItem> conflicts = spaceBlackoutMapper.selectConflictReservationItems(blackout);
        if (conflicts != null && !conflicts.isEmpty())
        {
            SpaceReservationItem item = conflicts.get(0);
            throw new ServiceException("维护停用时间与已有预约冲突：" + buildConflictReason(item));
        }
    }

    private LocalDateTime parseDateTime(String value, String message)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new ServiceException(message);
        }
        try
        {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        }
        catch (DateTimeParseException e)
        {
            throw new ServiceException(message);
        }
    }

    private void lockBlackoutDays(Long roomId, LocalDate startDate, LocalDate endDate, String operator)
    {
        LocalDate current = startDate;
        while (!current.isAfter(endDate))
        {
            String bookingDate = DATE_FORMATTER.format(current);
            spaceRoomDayLockMapper.insertIgnore(roomId, bookingDate, operator);
            spaceRoomDayLockMapper.lockRoomDay(roomId, bookingDate);
            current = current.plusDays(1);
        }
    }

    private String buildConflictReason(SpaceReservationItem item)
    {
        return item.getRoomCode() + " " + item.getBookingDate() + " " + item.getStartTime() + "-" + item.getEndTime();
    }
}
