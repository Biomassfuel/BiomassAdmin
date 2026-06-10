package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceAuditLog;

public interface ISpaceAuditLogService
{
    public SpaceAuditLog selectSpaceAuditLogById(Long logId);
    public List<SpaceAuditLog> selectSpaceAuditLogList(SpaceAuditLog spaceAuditLog);
    public int insertSpaceAuditLog(SpaceAuditLog spaceAuditLog);
    public int updateSpaceAuditLog(SpaceAuditLog spaceAuditLog);
    public int deleteSpaceAuditLogByIds(Long[] logIds);
    public int deleteSpaceAuditLogById(Long logId);
}