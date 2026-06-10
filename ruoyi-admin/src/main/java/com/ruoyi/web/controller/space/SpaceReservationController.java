package com.ruoyi.web.controller.space;

import java.util.List;
import java.util.stream.Collectors;
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
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.space.SpaceAuditLog;
import com.ruoyi.system.domain.space.SpaceReservation;
import com.ruoyi.system.service.space.ISpaceReservationService;

@RestController
@RequestMapping("/space/reservation")
public class SpaceReservationController extends BaseController
{
    @Autowired
    private ISpaceReservationService spaceReservationService;

    @PreAuthorize("@ss.hasPermi('space:reservation:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceReservation spaceReservation)
    {
        startPage();
        List<SpaceReservation> list = spaceReservationService.selectSpaceReservationList(spaceReservation);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:reservation:export')")
    @Log(title = "预约申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SpaceReservation spaceReservation)
    {
        List<SpaceReservation> list = spaceReservationService.selectSpaceReservationList(spaceReservation);
        ExcelUtil<SpaceReservation> util = new ExcelUtil<SpaceReservation>(SpaceReservation.class);
        util.exportExcel(response, list, "预约申请数据");
    }

    @PreAuthorize("@ss.hasPermi('space:reservation:mine')")
    @GetMapping("/my/list")
    public TableDataInfo myList(SpaceReservation spaceReservation)
    {
        spaceReservation.setApplicantId(getUserId());
        startPage();
        List<SpaceReservation> list = spaceReservationService.selectSpaceReservationList(spaceReservation);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:audit:list')")
    @GetMapping("/pending/list")
    public TableDataInfo pendingList(SpaceReservation spaceReservation)
    {
        spaceReservation.setPendingOnly(true);
        startPage();
        List<SpaceReservation> list = spaceReservationService.selectSpaceReservationList(spaceReservation);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:reservation:query')")
    @GetMapping(value = "/{reservationId}")
    public AjaxResult getInfo(@PathVariable Long reservationId)
    {
        return success(spaceReservationService.selectSpaceReservationById(reservationId));
    }

    @PreAuthorize("@ss.hasPermi('space:reservation:add')")
    @Log(title = "预约申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceReservation spaceReservation)
    {
        SysUser user = getLoginUser().getUser();
        SysDept dept = user == null ? null : user.getDept();
        spaceReservation.setApplicantId(getUserId());
        spaceReservation.setApplicantName(user != null ? user.getNickName() : getUsername());
        spaceReservation.setApplicantPhone(user != null ? user.getPhonenumber() : "");
        spaceReservation.setApplicantRole(resolveApplicantRole(user));
        spaceReservation.setOrgName(dept != null ? dept.getDeptName() : "");
        spaceReservation.setCreateBy(getUsername());
        return toAjax(spaceReservationService.insertSpaceReservation(spaceReservation));
    }

    @PreAuthorize("@ss.hasPermi('space:reservation:edit')")
    @Log(title = "预约申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceReservation spaceReservation)
    {
        spaceReservation.setUpdateBy(getUsername());
        return toAjax(spaceReservationService.updateSpaceReservation(spaceReservation));
    }

    @PreAuthorize("@ss.hasPermi('space:reservation:cancel')")
    @Log(title = "预约申请", businessType = BusinessType.UPDATE)
    @PutMapping("/{reservationId}/cancel")
    public AjaxResult cancel(@PathVariable Long reservationId)
    {
        return toAjax(spaceReservationService.cancelReservation(reservationId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('space:audit:approve')")
    @Log(title = "预约审核", businessType = BusinessType.UPDATE)
    @PutMapping("/{reservationId}/approve")
    public AjaxResult approve(@PathVariable Long reservationId, @RequestBody(required = false) SpaceAuditLog auditLog)
    {
        String opinion = auditLog == null ? "审核通过" : auditLog.getAuditOpinion();
        return toAjax(spaceReservationService.approveReservation(reservationId, getUserId(), getUsername(), opinion, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('space:audit:reject')")
    @Log(title = "预约审核", businessType = BusinessType.UPDATE)
    @PutMapping("/{reservationId}/reject")
    public AjaxResult reject(@PathVariable Long reservationId, @RequestBody(required = false) SpaceAuditLog auditLog)
    {
        String reason = auditLog == null ? "审核驳回" : auditLog.getAuditOpinion();
        return toAjax(spaceReservationService.rejectReservation(reservationId, getUserId(), getUsername(), reason, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('space:audit:approve')")
    @Log(title = "场次审核", businessType = BusinessType.UPDATE)
    @PutMapping("/item/{itemId}/approve")
    public AjaxResult approveItem(@PathVariable Long itemId, @RequestBody(required = false) SpaceAuditLog auditLog)
    {
        String opinion = auditLog == null ? "单场次通过" : auditLog.getAuditOpinion();
        return toAjax(spaceReservationService.approveItem(itemId, getUserId(), getUsername(), opinion, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('space:audit:reject')")
    @Log(title = "场次审核", businessType = BusinessType.UPDATE)
    @PutMapping("/item/{itemId}/reject")
    public AjaxResult rejectItem(@PathVariable Long itemId, @RequestBody(required = false) SpaceAuditLog auditLog)
    {
        String reason = auditLog == null ? "单场次驳回" : auditLog.getAuditOpinion();
        return toAjax(spaceReservationService.rejectItem(itemId, getUserId(), getUsername(), reason, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('space:reservation:remove')")
    @Log(title = "预约申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{reservationIds}")
    public AjaxResult remove(@PathVariable Long[] reservationIds)
    {
        return toAjax(spaceReservationService.deleteSpaceReservationByIds(reservationIds));
    }

    private String resolveApplicantRole(SysUser user)
    {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty())
        {
            return "";
        }
        return user.getRoles().stream()
                .filter(role -> role != null && StringUtils.isNotEmpty(role.getRoleName()))
                .map(role -> role.getRoleName())
                .collect(Collectors.joining(","));
    }
}
