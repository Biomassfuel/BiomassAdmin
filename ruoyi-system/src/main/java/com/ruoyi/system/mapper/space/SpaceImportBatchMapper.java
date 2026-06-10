package com.ruoyi.system.mapper.space;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.space.SpaceImportBatch;

public interface SpaceImportBatchMapper
{
    public SpaceImportBatch selectSpaceImportBatchById(Long batchId);
    public List<SpaceImportBatch> selectSpaceImportBatchList(SpaceImportBatch spaceImportBatch);
    public int insertSpaceImportBatch(SpaceImportBatch spaceImportBatch);
    public int updateSpaceImportBatch(SpaceImportBatch spaceImportBatch);
    public int deleteSpaceImportBatchById(Long batchId);
    public int deleteSpaceImportBatchByIds(Long[] batchIds);
}