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
import com.ruoyi.system.domain.space.SpaceTimePeriod;
import com.ruoyi.system.service.space.ISpaceTimePeriodService;

@RestController
@RequestMapping("/space/time-period")
public class SpaceTimePeriodController extends BaseController
{
    @Autowired
    private ISpaceTimePeriodService spaceTimePeriodService;

    @PreAuthorize("@ss.hasPermi('space:timePeriod:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceTimePeriod spaceTimePeriod)
    {
        startPage();
        List<SpaceTimePeriod> list = spaceTimePeriodService.selectSpaceTimePeriodList(spaceTimePeriod);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:reservationItem:publicList')")
    @GetMapping("/public/list")
    public TableDataInfo publicList(SpaceTimePeriod spaceTimePeriod)
    {
        spaceTimePeriod.setStatus("0");
        startPage();
        List<SpaceTimePeriod> list = spaceTimePeriodService.selectSpaceTimePeriodList(spaceTimePeriod);
        list.forEach(this::sanitizePublicTimePeriod);
        return getDataTable(list);
    }

    private void sanitizePublicTimePeriod(SpaceTimePeriod period)
    {
        if (period == null)
        {
            return;
        }
        period.setCreateBy(null);
        period.setUpdateBy(null);
        period.setRemark(null);
    }

    @PreAuthorize("@ss.hasPermi('space:timePeriod:query')")
    @GetMapping(value = "/{periodId}")
    public AjaxResult getInfo(@PathVariable Long periodId)
    {
        return success(spaceTimePeriodService.selectSpaceTimePeriodById(periodId));
    }

    @PreAuthorize("@ss.hasPermi('space:timePeriod:add')")
    @Log(title = "标准时段", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceTimePeriod spaceTimePeriod)
    {
        spaceTimePeriod.setCreateBy(getUsername());
        return toAjax(spaceTimePeriodService.insertSpaceTimePeriod(spaceTimePeriod));
    }

    @PreAuthorize("@ss.hasPermi('space:timePeriod:edit')")
    @Log(title = "标准时段", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceTimePeriod spaceTimePeriod)
    {
        spaceTimePeriod.setUpdateBy(getUsername());
        return toAjax(spaceTimePeriodService.updateSpaceTimePeriod(spaceTimePeriod));
    }

    @PreAuthorize("@ss.hasPermi('space:timePeriod:remove')")
    @Log(title = "标准时段", businessType = BusinessType.DELETE)
    @DeleteMapping("/{periodIds}")
    public AjaxResult remove(@PathVariable Long[] periodIds)
    {
        return toAjax(spaceTimePeriodService.deleteSpaceTimePeriodByIds(periodIds));
    }
}
