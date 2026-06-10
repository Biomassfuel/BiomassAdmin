package com.ruoyi.web.controller.space;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.space.SpaceImportBatch;
import com.ruoyi.system.service.space.ISpaceImportBatchService;

@RestController
@RequestMapping("/space/import-batch")
public class SpaceImportBatchController extends BaseController
{
    @Autowired
    private ISpaceImportBatchService spaceImportBatchService;

    @PreAuthorize("@ss.hasPermi('space:import:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceImportBatch spaceImportBatch)
    {
        startPage();
        List<SpaceImportBatch> list = spaceImportBatchService.selectSpaceImportBatchList(spaceImportBatch);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:import:query')")
    @GetMapping(value = "/{batchId}")
    public AjaxResult getInfo(@PathVariable Long batchId)
    {
        return success(spaceImportBatchService.selectSpaceImportBatchById(batchId));
    }

    @PreAuthorize("@ss.hasPermi('space:import:add')")
    @Log(title = "导入批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceImportBatch spaceImportBatch)
    {
        spaceImportBatch.setCreateBy(getUsername());
        return toAjax(spaceImportBatchService.insertSpaceImportBatch(spaceImportBatch));
    }

    @PreAuthorize("@ss.hasPermi('space:import:edit')")
    @Log(title = "导入批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceImportBatch spaceImportBatch)
    {
        spaceImportBatch.setUpdateBy(getUsername());
        return toAjax(spaceImportBatchService.updateSpaceImportBatch(spaceImportBatch));
    }

    @PreAuthorize("@ss.hasPermi('space:import:remove')")
    @Log(title = "导入批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{batchIds}")
    public AjaxResult remove(@PathVariable Long[] batchIds)
    {
        return toAjax(spaceImportBatchService.deleteSpaceImportBatchByIds(batchIds));
    }
}