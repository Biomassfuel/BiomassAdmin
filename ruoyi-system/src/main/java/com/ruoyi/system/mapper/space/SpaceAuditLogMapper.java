package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceAuditLog;

public interface SpaceAuditLogMapper
{
    public SpaceAuditLog selectSpaceAuditLogById(Long logId);
    public List<SpaceAuditLog> selectSpaceAuditLogList(SpaceAuditLog spaceAuditLog);
    public int insertSpaceAuditLog(SpaceAuditLog spaceAuditLog);
    public int updateSpaceAuditLog(SpaceAuditLog spaceAuditLog);
    public int deleteSpaceAuditLogById(Long logId);
    public int deleteSpaceAuditLogByIds(Long[] logIds);
}