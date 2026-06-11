package com.ruoyi.system.service.space.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.space.SpaceImportBatch;
import com.ruoyi.system.domain.space.SpaceReservationItem;
import com.ruoyi.system.domain.space.SpaceRoom;
import com.ruoyi.system.domain.space.SpaceRoomEquipment;
import com.ruoyi.system.mapper.space.SpaceImportBatchMapper;
import com.ruoyi.system.mapper.space.SpaceReservationItemMapper;
import com.ruoyi.system.mapper.space.SpaceRoomEquipmentMapper;
import com.ruoyi.system.mapper.space.SpaceRoomMapper;
import com.ruoyi.system.service.space.ISpaceRoomService;

@Service
public class SpaceRoomServiceImpl implements ISpaceRoomService
{
    @Autowired
    private SpaceRoomMapper spaceRoomMapper;

    @Autowired
    private SpaceImportBatchMapper spaceImportBatchMapper;

    @Autowired
    private SpaceRoomEquipmentMapper spaceRoomEquipmentMapper;

    @Autowired
    private SpaceReservationItemMapper spaceReservationItemMapper;

    @Override
    public SpaceRoom selectSpaceRoomById(Long roomId)
    {
        SpaceRoom room = spaceRoomMapper.selectSpaceRoomById(roomId);
        if (room != null)
        {
            SpaceRoomEquipment query = new SpaceRoomEquipment();
            query.setRoomId(roomId);
            room.setRoomEquipmentList(spaceRoomEquipmentMapper.selectSpaceRoomEquipmentList(query));
        }
        return room;
    }

    @Override
    public List<SpaceRoom> selectSpaceRoomList(SpaceRoom spaceRoom)
    {
        return spaceRoomMapper.selectSpaceRoomList(spaceRoom);
    }

    @Override
    public List<SpaceRoom> selectDeletedSpaceRoomList(SpaceRoom spaceRoom)
    {
        return spaceRoomMapper.selectDeletedSpaceRoomList(spaceRoom);
    }

    @Override
    @Transactional
    public int insertSpaceRoom(SpaceRoom spaceRoom)
    {
        fillRoomDefaults(spaceRoom);
        validateRoomCodeUnique(spaceRoom);
        int rows = spaceRoomMapper.insertSpaceRoom(spaceRoom);
        saveRoomEquipment(spaceRoom);
        return rows;
    }

    @Override
    @Transactional
    public int updateSpaceRoom(SpaceRoom spaceRoom)
    {
        fillRoomDefaults(spaceRoom);
        validateRoomCodeUnique(spaceRoom);
        int rows = spaceRoomMapper.updateSpaceRoom(spaceRoom);
        saveRoomEquipment(spaceRoom);
        return rows;
    }

    @Override
    @Transactional
    public int deleteSpaceRoomByIds(Long[] roomIds)
    {
        assertNoActiveReservations(roomIds);
        return spaceRoomMapper.deleteSpaceRoomByIds(roomIds);
    }

    @Override
    @Transactional
    public int deleteSpaceRoomById(Long roomId)
    {
        assertNoActiveReservations(new Long[] { roomId });
        return spaceRoomMapper.deleteSpaceRoomById(roomId);
    }

    @Override
    public int restoreSpaceRoomByIds(Long[] roomIds)
    {
        return spaceRoomMapper.restoreSpaceRoomByIds(roomIds);
    }

    @Override
    @Transactional
    public int forceDeleteSpaceRoomByIds(Long[] roomIds)
    {
        assertNoActiveReservations(roomIds);
        spaceRoomEquipmentMapper.deleteSpaceRoomEquipmentByRoomIds(roomIds);
        return spaceRoomMapper.forceDeleteSpaceRoomByIds(roomIds);
    }

    @Override
    @Transactional
    public String importRoom(List<SpaceRoom> roomList, Boolean isUpdateSupport, String operName, String fileName)
    {
        if (StringUtils.isNull(roomList) || roomList.isEmpty())
        {
            throw new ServiceException("导入房间数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (SpaceRoom room : roomList)
        {
            try
            {
                if (StringUtils.isBlank(room.getRoomCode()))
                {
                    throw new ServiceException("房间编号不能为空");
                }

                SpaceRoom existing = spaceRoomMapper.selectSpaceRoomByCodeAll(room.getRoomCode());
                if (existing == null)
                {
                    fillRoomDefaults(room);
                    room.setCreateBy(operName);
                    spaceRoomMapper.insertSpaceRoom(room);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、房间 ").append(room.getRoomCode()).append(" 导入成功");
                }
                else if ("2".equals(existing.getDelFlag()))
                {
                    throw new ServiceException("房间编号已在回收站中，请先恢复或永久删除");
                }
                else if (isUpdateSupport)
                {
                    room.setRoomId(existing.getRoomId());
                    fillRoomDefaults(room);
                    room.setUpdateBy(operName);
                    spaceRoomMapper.updateSpaceRoom(room);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、房间 ").append(room.getRoomCode()).append(" 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、房间 ").append(room.getRoomCode()).append(" 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String roomCode = StringUtils.isBlank(room.getRoomCode()) ? "空编号" : room.getRoomCode();
                failureMsg.append("<br/>").append(failureNum).append("、房间 ").append(roomCode).append(" 导入失败：").append(e.getMessage());
            }
        }

        recordImportBatch(fileName, roomList.size(), successNum, failureNum, failureMsg.toString(), operName);
        if (failureNum > 0)
        {
            failureMsg.insert(0, "房间导入完成，失败 " + failureNum + " 条，成功 " + successNum + " 条，明细如下：");
            return failureMsg.toString();
        }

        successMsg.insert(0, "房间导入成功，共 " + successNum + " 条。");
        return successMsg.toString();
    }

    private void fillRoomDefaults(SpaceRoom room)
    {
        if (StringUtils.isBlank(room.getRoomName()))
        {
            room.setRoomName(room.getRoomCode());
        }
        if (StringUtils.isBlank(room.getBookable()))
        {
            room.setBookable("0");
        }
        if (StringUtils.isBlank(room.getStatus()))
        {
            room.setStatus("0");
        }
        if (StringUtils.isBlank(room.getDelFlag()))
        {
            room.setDelFlag("0");
        }
    }

    private void validateRoomCodeUnique(SpaceRoom room)
    {
        if (StringUtils.isBlank(room.getRoomCode()))
        {
            throw new ServiceException("房间编号不能为空");
        }
        SpaceRoom existing = spaceRoomMapper.selectSpaceRoomByCodeAll(room.getRoomCode());
        if (existing != null && (room.getRoomId() == null || !existing.getRoomId().equals(room.getRoomId())))
        {
            if ("2".equals(existing.getDelFlag()))
            {
                throw new ServiceException("房间编号“" + room.getRoomCode() + "”已在回收站中，请先恢复或永久删除");
            }
            throw new ServiceException("房间编号“" + room.getRoomCode() + "”已存在");
        }
    }

    private void assertNoActiveReservations(Long[] roomIds)
    {
        if (roomIds == null || roomIds.length == 0)
        {
            return;
        }
        List<SpaceReservationItem> blockingItems = spaceReservationItemMapper.selectBlockingRoomReservationItems(roomIds);
        if (blockingItems == null || blockingItems.isEmpty())
        {
            return;
        }
        SpaceReservationItem item = blockingItems.get(0);
        String roomText = StringUtils.isBlank(item.getRoomCode()) ? String.valueOf(item.getRoomId()) : item.getRoomCode();
        String reservationText = StringUtils.isBlank(item.getReservationNo()) ? String.valueOf(item.getReservationId()) : item.getReservationNo();
        throw new ServiceException("房间 " + roomText + " 存在未结束预约 " + reservationText + "（" + item.getBookingDate() + " " + item.getStartTime() + "-" + item.getEndTime() + "），不能删除，请先取消预约或等待结束");
    }

    private void saveRoomEquipment(SpaceRoom room)
    {
        if (room.getRoomId() == null || room.getRoomEquipmentList() == null)
        {
            return;
        }
        spaceRoomEquipmentMapper.deleteSpaceRoomEquipmentByRoomId(room.getRoomId());
        if (room.getRoomEquipmentList().isEmpty())
        {
            return;
        }
        List<SpaceRoomEquipment> validList = new ArrayList<>();
        Set<Long> equipmentIds = new HashSet<>();
        for (SpaceRoomEquipment item : room.getRoomEquipmentList())
        {
            if (item.getEquipmentId() == null)
            {
                continue;
            }
            if (!equipmentIds.add(item.getEquipmentId()))
            {
                throw new ServiceException("同一房间不能重复配置同一个设备");
            }
            item.setRoomId(room.getRoomId());
            if (item.getQuantity() == null || item.getQuantity() < 1)
            {
                item.setQuantity(1);
            }
            if (StringUtils.isBlank(item.getStatus()))
            {
                item.setStatus("0");
            }
            if (StringUtils.isBlank(item.getCreateBy()))
            {
                item.setCreateBy(StringUtils.isBlank(room.getUpdateBy()) ? room.getCreateBy() : room.getUpdateBy());
            }
            validList.add(item);
        }
        if (!validList.isEmpty())
        {
            spaceRoomEquipmentMapper.batchInsertSpaceRoomEquipment(validList);
        }
    }

    private void recordImportBatch(String fileName, int totalCount, int successCount, int failCount, String errorMsg, String operName)
    {
        SpaceImportBatch batch = new SpaceImportBatch();
        batch.setImportType("room");
        batch.setFileName(fileName);
        batch.setFilePath("");
        batch.setTotalCount(totalCount);
        batch.setSuccessCount(successCount);
        batch.setFailCount(failCount);
        batch.setImportStatus(failCount == 0 ? "2" : (successCount > 0 ? "3" : "4"));
        batch.setErrorMsg(errorMsg);
        batch.setCreateBy(operName);
        spaceImportBatchMapper.insertSpaceImportBatch(batch);
    }
}
