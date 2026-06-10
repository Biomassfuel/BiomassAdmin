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
import com.ruoyi.system.domain.space.SpaceBlackout;
import com.ruoyi.system.service.space.ISpaceBlackoutService;

@RestController
@RequestMapping("/space/blackout")
public class SpaceBlackoutController extends BaseController
{
    @Autowired
    private ISpaceBlackoutService spaceBlackoutService;

    @PreAuthorize("@ss.hasPermi('space:blackout:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceBlackout spaceBlackout)
    {
        startPage();
        List<SpaceBlackout> list = spaceBlackoutService.selectSpaceBlackoutList(spaceBlackout);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:blackout:query')")
    @GetMapping(value = "/{blackoutId}")
    public AjaxResult getInfo(@PathVariable Long blackoutId)
    {
        return success(spaceBlackoutService.selectSpaceBlackoutById(blackoutId));
    }

    @PreAuthorize("@ss.hasPermi('space:blackout:add')")
    @Log(title = "维护停用", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceBlackout spaceBlackout)
    {
        spaceBlackout.setCreateBy(getUsername());
        return toAjax(spaceBlackoutService.insertSpaceBlackout(spaceBlackout));
    }

    @PreAuthorize("@ss.hasPermi('space:blackout:edit')")
    @Log(title = "维护停用", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceBlackout spaceBlackout)
    {
        spaceBlackout.setUpdateBy(getUsername());
        return toAjax(spaceBlackoutService.updateSpaceBlackout(spaceBlackout));
    }

    @PreAuthorize("@ss.hasPermi('space:blackout:remove')")
    @Log(title = "维护停用", businessType = BusinessType.DELETE)
    @DeleteMapping("/{blackoutIds}")
    public AjaxResult remove(@PathVariable Long[] blackoutIds)
    {
        return toAjax(spaceBlackoutService.deleteSpaceBlackoutByIds(blackoutIds));
    }
}