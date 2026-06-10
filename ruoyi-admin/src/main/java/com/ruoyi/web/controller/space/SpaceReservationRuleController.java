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
import com.ruoyi.system.domain.space.SpaceReservationRule;
import com.ruoyi.system.service.space.ISpaceReservationRuleService;

@RestController
@RequestMapping("/space/reservation/rule")
public class SpaceReservationRuleController extends BaseController
{
    @Autowired
    private ISpaceReservationRuleService spaceReservationRuleService;

    @PreAuthorize("@ss.hasPermi('space:reservationRule:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceReservationRule spaceReservationRule)
    {
        startPage();
        List<SpaceReservationRule> list = spaceReservationRuleService.selectSpaceReservationRuleList(spaceReservationRule);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:reservationRule:query')")
    @GetMapping(value = "/{ruleId}")
    public AjaxResult getInfo(@PathVariable Long ruleId)
    {
        return success(spaceReservationRuleService.selectSpaceReservationRuleById(ruleId));
    }

    @PreAuthorize("@ss.hasPermi('space:reservationRule:add')")
    @Log(title = "长期预约规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceReservationRule spaceReservationRule)
    {
        spaceReservationRule.setCreateBy(getUsername());
        return toAjax(spaceReservationRuleService.insertSpaceReservationRule(spaceReservationRule));
    }

    @PreAuthorize("@ss.hasPermi('space:reservationRule:edit')")
    @Log(title = "长期预约规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceReservationRule spaceReservationRule)
    {
        spaceReservationRule.setUpdateBy(getUsername());
        return toAjax(spaceReservationRuleService.updateSpaceReservationRule(spaceReservationRule));
    }

    @PreAuthorize("@ss.hasPermi('space:reservationRule:remove')")
    @Log(title = "长期预约规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds)
    {
        return toAjax(spaceReservationRuleService.deleteSpaceReservationRuleByIds(ruleIds));
    }
}