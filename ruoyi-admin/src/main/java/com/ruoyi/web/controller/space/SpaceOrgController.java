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
import com.ruoyi.system.domain.space.SpaceOrg;
import com.ruoyi.system.service.space.ISpaceOrgService;

@RestController
@RequestMapping("/space/org")
public class SpaceOrgController extends BaseController
{
    @Autowired
    private ISpaceOrgService spaceOrgService;

    @PreAuthorize("@ss.hasPermi('space:org:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceOrg spaceOrg)
    {
        startPage();
        List<SpaceOrg> list = spaceOrgService.selectSpaceOrgList(spaceOrg);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:org:query')")
    @GetMapping(value = "/{orgId}")
    public AjaxResult getInfo(@PathVariable Long orgId)
    {
        return success(spaceOrgService.selectSpaceOrgById(orgId));
    }

    @PreAuthorize("@ss.hasPermi('space:org:add')")
    @Log(title = "学校/组织", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceOrg spaceOrg)
    {
        spaceOrg.setCreateBy(getUsername());
        return toAjax(spaceOrgService.insertSpaceOrg(spaceOrg));
    }

    @PreAuthorize("@ss.hasPermi('space:org:edit')")
    @Log(title = "学校/组织", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceOrg spaceOrg)
    {
        spaceOrg.setUpdateBy(getUsername());
        return toAjax(spaceOrgService.updateSpaceOrg(spaceOrg));
    }

    @PreAuthorize("@ss.hasPermi('space:org:remove')")
    @Log(title = "学校/组织", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orgIds}")
    public AjaxResult remove(@PathVariable Long[] orgIds)
    {
        return toAjax(spaceOrgService.deleteSpaceOrgByIds(orgIds));
    }
}