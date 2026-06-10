package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.space.SpaceImportBatch;
import com.ruoyi.system.domain.space.SpaceRoom;
import com.ruoyi.system.mapper.space.SpaceImportBatchMapper;
import com.ruoyi.system.mapper.space.SpaceRoomMapper;
import com.ruoyi.system.service.space.ISpaceRoomService;

@Service
public class SpaceRoomServiceImpl implements ISpaceRoomService
{
    @Autowired
    private SpaceRoomMapper spaceRoomMapper;

    @Autowired
    private SpaceImportBatchMapper spaceImportBatchMapper;

    @Override
    public SpaceRoom selectSpaceRoomById(Long roomId)
    {
        return spaceRoomMapper.selectSpaceRoomById(roomId);
    }

    @Override
    public List<SpaceRoom> selectSpaceRoomList(SpaceRoom spaceRoom)
    {
        return spaceRoomMapper.selectSpaceRoomList(spaceRoom);
    }

    @Override
    public int insertSpaceRoom(SpaceRoom spaceRoom)
    {
        fillRoomDefaults(spaceRoom);
        return spaceRoomMapper.insertSpaceRoom(spaceRoom);
    }

    @Override
    public int updateSpaceRoom(SpaceRoom spaceRoom)
    {
        fillRoomDefaults(spaceRoom);
        return spaceRoomMapper.updateSpaceRoom(spaceRoom);
    }

    @Override
    public int deleteSpaceRoomByIds(Long[] roomIds)
    {
        return spaceRoomMapper.deleteSpaceRoomByIds(roomIds);
    }

    @Override
    public int deleteSpaceRoomById(Long roomId)
    {
        return spaceRoomMapper.deleteSpaceRoomById(roomId);
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

                SpaceRoom existing = spaceRoomMapper.selectSpaceRoomByCode(room.getRoomCode());
                if (existing == null)
                {
                    fillRoomDefaults(room);
                    room.setCreateBy(operName);
                    spaceRoomMapper.insertSpaceRoom(room);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、房间 ").append(room.getRoomCode()).append(" 导入成功");
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
