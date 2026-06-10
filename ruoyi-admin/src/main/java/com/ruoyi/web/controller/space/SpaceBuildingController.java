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
import com.ruoyi.system.domain.space.SpaceBuilding;
import com.ruoyi.system.service.space.ISpaceBuildingService;

@RestController
@RequestMapping("/space/building")
public class SpaceBuildingController extends BaseController
{
    @Autowired
    private ISpaceBuildingService spaceBuildingService;

    @PreAuthorize("@ss.hasPermi('space:building:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceBuilding spaceBuilding)
    {
        startPage();
        List<SpaceBuilding> list = spaceBuildingService.selectSpaceBuildingList(spaceBuilding);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:building:query')")
    @GetMapping(value = "/{buildingId}")
    public AjaxResult getInfo(@PathVariable Long buildingId)
    {
        return success(spaceBuildingService.selectSpaceBuildingById(buildingId));
    }

    @PreAuthorize("@ss.hasPermi('space:building:add')")
    @Log(title = "楼栋", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceBuilding spaceBuilding)
    {
        spaceBuilding.setCreateBy(getUsername());
        return toAjax(spaceBuildingService.insertSpaceBuilding(spaceBuilding));
    }

    @PreAuthorize("@ss.hasPermi('space:building:edit')")
    @Log(title = "楼栋", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceBuilding spaceBuilding)
    {
        spaceBuilding.setUpdateBy(getUsername());
        return toAjax(spaceBuildingService.updateSpaceBuilding(spaceBuilding));
    }

    @PreAuthorize("@ss.hasPermi('space:building:remove')")
    @Log(title = "楼栋", businessType = BusinessType.DELETE)
    @DeleteMapping("/{buildingIds}")
    public AjaxResult remove(@PathVariable Long[] buildingIds)
    {
        return toAjax(spaceBuildingService.deleteSpaceBuildingByIds(buildingIds));
    }
}