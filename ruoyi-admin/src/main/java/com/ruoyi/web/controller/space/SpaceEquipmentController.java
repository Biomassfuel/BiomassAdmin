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
import com.ruoyi.system.domain.space.SpaceEquipment;
import com.ruoyi.system.service.space.ISpaceEquipmentService;

@RestController
@RequestMapping("/space/equipment")
public class SpaceEquipmentController extends BaseController
{
    @Autowired
    private ISpaceEquipmentService spaceEquipmentService;

    @PreAuthorize("@ss.hasPermi('space:equipment:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceEquipment spaceEquipment)
    {
        startPage();
        List<SpaceEquipment> list = spaceEquipmentService.selectSpaceEquipmentList(spaceEquipment);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:equipment:export')")
    @Log(title = "设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SpaceEquipment spaceEquipment)
    {
        List<SpaceEquipment> list = spaceEquipmentService.selectSpaceEquipmentList(spaceEquipment);
        ExcelUtil<SpaceEquipment> util = new ExcelUtil<SpaceEquipment>(SpaceEquipment.class);
        util.exportExcel(response, list, "设备数据");
    }

    @PreAuthorize("@ss.hasPermi('space:equipment:query')")
    @GetMapping(value = "/{equipmentId}")
    public AjaxResult getInfo(@PathVariable Long equipmentId)
    {
        return success(spaceEquipmentService.selectSpaceEquipmentById(equipmentId));
    }

    @PreAuthorize("@ss.hasPermi('space:equipment:add')")
    @Log(title = "设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceEquipment spaceEquipment)
    {
        spaceEquipment.setCreateBy(getUsername());
        return toAjax(spaceEquipmentService.insertSpaceEquipment(spaceEquipment));
    }

    @PreAuthorize("@ss.hasPermi('space:equipment:edit')")
    @Log(title = "设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceEquipment spaceEquipment)
    {
        spaceEquipment.setUpdateBy(getUsername());
        return toAjax(spaceEquipmentService.updateSpaceEquipment(spaceEquipment));
    }

    @PreAuthorize("@ss.hasPermi('space:equipment:remove')")
    @Log(title = "设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{equipmentIds}")
    public AjaxResult remove(@PathVariable Long[] equipmentIds)
    {
        return toAjax(spaceEquipmentService.deleteSpaceEquipmentByIds(equipmentIds));
    }
}
