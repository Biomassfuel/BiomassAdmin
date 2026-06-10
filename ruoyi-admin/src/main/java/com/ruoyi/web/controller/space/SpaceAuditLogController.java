package com.ruoyi.web.controller.space;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.space.SpaceAuditLog;
import com.ruoyi.system.service.space.ISpaceAuditLogService;

@RestController
@RequestMapping("/space/audit-log")
public class SpaceAuditLogController extends BaseController
{
    @Autowired
    private ISpaceAuditLogService spaceAuditLogService;

    @PreAuthorize("@ss.hasPermi('space:auditLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceAuditLog spaceAuditLog)
    {
        startPage();
        List<SpaceAuditLog> list = spaceAuditLogService.selectSpaceAuditLogList(spaceAuditLog);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:auditLog:export')")
    @Log(title = "审核日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SpaceAuditLog spaceAuditLog)
    {
        List<SpaceAuditLog> list = spaceAuditLogService.selectSpaceAuditLogList(spaceAuditLog);
        ExcelUtil<SpaceAuditLog> util = new ExcelUtil<SpaceAuditLog>(SpaceAuditLog.class);
        util.exportExcel(response, list, "审核日志数据");
    }

    @PreAuthorize("@ss.hasPermi('space:auditLog:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(@PathVariable Long logId)
    {
        return success(spaceAuditLogService.selectSpaceAuditLogById(logId));
    }

    @PreAuthorize("@ss.hasPermi('space:auditLog:add')")
    @Log(title = "审核日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceAuditLog spaceAuditLog)
    {
        spaceAuditLog.setCreateBy(getUsername());
        return toAjax(spaceAuditLogService.insertSpaceAuditLog(spaceAuditLog));
    }

    @PreAuthorize("@ss.hasPermi('space:auditLog:edit')")
    @Log(title = "审核日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceAuditLog spaceAuditLog)
    {
        spaceAuditLog.setUpdateBy(getUsername());
        return toAjax(spaceAuditLogService.updateSpaceAuditLog(spaceAuditLog));
    }

    @PreAuthorize("@ss.hasPermi('space:auditLog:remove')")
    @Log(title = "审核日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(spaceAuditLogService.deleteSpaceAuditLogByIds(logIds));
    }
}
