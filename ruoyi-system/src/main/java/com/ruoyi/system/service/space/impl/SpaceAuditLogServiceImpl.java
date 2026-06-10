package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.space.SpaceAuditLog;
import com.ruoyi.system.mapper.space.SpaceAuditLogMapper;
import com.ruoyi.system.service.space.ISpaceAuditLogService;

@Service
public class SpaceAuditLogServiceImpl implements ISpaceAuditLogService
{
    @Autowired
    private SpaceAuditLogMapper spaceAuditLogMapper;

    @Override
    public SpaceAuditLog selectSpaceAuditLogById(Long logId)
    {
        return spaceAuditLogMapper.selectSpaceAuditLogById(logId);
    }

    @Override
    public List<SpaceAuditLog> selectSpaceAuditLogList(SpaceAuditLog spaceAuditLog)
    {
        return spaceAuditLogMapper.selectSpaceAuditLogList(spaceAuditLog);
    }

    @Override
    public int insertSpaceAuditLog(SpaceAuditLog spaceAuditLog)
    {
        return spaceAuditLogMapper.insertSpaceAuditLog(spaceAuditLog);
    }

    @Override
    public int updateSpaceAuditLog(SpaceAuditLog spaceAuditLog)
    {
        return spaceAuditLogMapper.updateSpaceAuditLog(spaceAuditLog);
    }

    @Override
    public int deleteSpaceAuditLogByIds(Long[] logIds)
    {
        return spaceAuditLogMapper.deleteSpaceAuditLogByIds(logIds);
    }

    @Override
    public int deleteSpaceAuditLogById(Long logId)
    {
        return spaceAuditLogMapper.deleteSpaceAuditLogById(logId);
    }
}