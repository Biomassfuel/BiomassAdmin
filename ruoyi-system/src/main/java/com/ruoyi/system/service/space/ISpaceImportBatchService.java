package com.ruoyi.system.service.space;

import java.util.List;
import com.ruoyi.system.domain.space.SpaceImportBatch;

public interface ISpaceImportBatchService
{
    public SpaceImportBatch selectSpaceImportBatchById(Long batchId);
    public List<SpaceImportBatch> selectSpaceImportBatchList(SpaceImportBatch spaceImportBatch);
    public int insertSpaceImportBatch(SpaceImportBatch spaceImportBatch);
    public int updateSpaceImportBatch(SpaceImportBatch spaceImportBatch);
    public int deleteSpaceImportBatchByIds(Long[] batchIds);
    public int deleteSpaceImportBatchById(Long batchId);
}