package com.ruoyi.system.service.space.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.space.SpaceImportBatch;
import com.ruoyi.system.mapper.space.SpaceImportBatchMapper;
import com.ruoyi.system.service.space.ISpaceImportBatchService;

@Service
public class SpaceImportBatchServiceImpl implements ISpaceImportBatchService
{
    @Autowired
    private SpaceImportBatchMapper spaceImportBatchMapper;

    @Override
    public SpaceImportBatch selectSpaceImportBatchById(Long batchId)
    {
        return spaceImportBatchMapper.selectSpaceImportBatchById(batchId);
    }

    @Override
    public List<SpaceImportBatch> selectSpaceImportBatchList(SpaceImportBatch spaceImportBatch)
    {
        return spaceImportBatchMapper.selectSpaceImportBatchList(spaceImportBatch);
    }

    @Override
    public int insertSpaceImportBatch(SpaceImportBatch spaceImportBatch)
    {
        return spaceImportBatchMapper.insertSpaceImportBatch(spaceImportBatch);
    }

    @Override
    public int updateSpaceImportBatch(SpaceImportBatch spaceImportBatch)
    {
        return spaceImportBatchMapper.updateSpaceImportBatch(spaceImportBatch);
    }

    @Override
    public int deleteSpaceImportBatchByIds(Long[] batchIds)
    {
        return spaceImportBatchMapper.deleteSpaceImportBatchByIds(batchIds);
    }

    @Override
    public int deleteSpaceImportBatchById(Long batchId)
    {
        return spaceImportBatchMapper.deleteSpaceImportBatchById(batchId);
    }
}