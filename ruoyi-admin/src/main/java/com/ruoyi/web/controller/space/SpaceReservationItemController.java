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
import com.ruoyi.system.domain.space.SpaceReservationItem;
import com.ruoyi.system.service.space.ISpaceReservationItemService;

@RestController
@RequestMapping("/space/reservation/item")
public class SpaceReservationItemController extends BaseController
{
    @Autowired
    private ISpaceReservationItemService spaceReservationItemService;

    @PreAuthorize("@ss.hasPermi('space:reservationItem:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceReservationItem spaceReservationItem)
    {
        startPage();
        List<SpaceReservationItem> list = spaceReservationItemService.selectSpaceReservationItemList(spaceReservationItem);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:reservationItem:export')")
    @Log(title = "预约场次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SpaceReservationItem spaceReservationItem)
    {
        List<SpaceReservationItem> list = spaceReservationItemService.selectSpaceReservationItemList(spaceReservationItem);
        ExcelUtil<SpaceReservationItem> util = new ExcelUtil<SpaceReservationItem>(SpaceReservationItem.class);
        util.exportExcel(response, list, "预约场次数据");
    }

    @PreAuthorize("@ss.hasPermi('space:reservationItem:query')")
    @GetMapping(value = "/{itemId}")
    public AjaxResult getInfo(@PathVariable Long itemId)
    {
        return success(spaceReservationItemService.selectSpaceReservationItemById(itemId));
    }

    @PreAuthorize("@ss.hasPermi('space:reservationItem:add')")
    @Log(title = "预约场次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceReservationItem spaceReservationItem)
    {
        spaceReservationItem.setCreateBy(getUsername());
        return toAjax(spaceReservationItemService.insertSpaceReservationItem(spaceReservationItem));
    }

    @PreAuthorize("@ss.hasPermi('space:reservationItem:edit')")
    @Log(title = "预约场次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceReservationItem spaceReservationItem)
    {
        spaceReservationItem.setUpdateBy(getUsername());
        return toAjax(spaceReservationItemService.updateSpaceReservationItem(spaceReservationItem));
    }

    @PreAuthorize("@ss.hasPermi('space:reservationItem:remove')")
    @Log(title = "预约场次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{itemIds}")
    public AjaxResult remove(@PathVariable Long[] itemIds)
    {
        return toAjax(spaceReservationItemService.deleteSpaceReservationItemByIds(itemIds));
    }
}
