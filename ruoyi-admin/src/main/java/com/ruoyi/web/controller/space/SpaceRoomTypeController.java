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
import com.ruoyi.system.domain.space.SpaceRoomType;
import com.ruoyi.system.service.space.ISpaceRoomTypeService;

@RestController
@RequestMapping("/space/room/type")
public class SpaceRoomTypeController extends BaseController
{
    @Autowired
    private ISpaceRoomTypeService spaceRoomTypeService;

    @PreAuthorize("@ss.hasPermi('space:roomType:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceRoomType spaceRoomType)
    {
        startPage();
        List<SpaceRoomType> list = spaceRoomTypeService.selectSpaceRoomTypeList(spaceRoomType);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:reservationItem:publicList')")
    @GetMapping("/public/list")
    public TableDataInfo publicList(SpaceRoomType spaceRoomType)
    {
        spaceRoomType.setStatus("0");
        startPage();
        List<SpaceRoomType> list = spaceRoomTypeService.selectSpaceRoomTypeList(spaceRoomType);
        list.forEach(this::sanitizePublicType);
        return getDataTable(list);
    }

    private void sanitizePublicType(SpaceRoomType type)
    {
        type.setCreateBy(null);
        type.setUpdateBy(null);
        type.setRemark(null);
    }

    @PreAuthorize("@ss.hasPermi('space:roomType:export')")
    @Log(title = "房间类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SpaceRoomType spaceRoomType)
    {
        List<SpaceRoomType> list = spaceRoomTypeService.selectSpaceRoomTypeList(spaceRoomType);
        ExcelUtil<SpaceRoomType> util = new ExcelUtil<SpaceRoomType>(SpaceRoomType.class);
        util.exportExcel(response, list, "房间类型数据");
    }

    @PreAuthorize("@ss.hasPermi('space:roomType:query')")
    @GetMapping(value = "/{typeId}")
    public AjaxResult getInfo(@PathVariable Long typeId)
    {
        return success(spaceRoomTypeService.selectSpaceRoomTypeById(typeId));
    }

    @PreAuthorize("@ss.hasPermi('space:roomType:add')")
    @Log(title = "房间类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceRoomType spaceRoomType)
    {
        spaceRoomType.setCreateBy(getUsername());
        return toAjax(spaceRoomTypeService.insertSpaceRoomType(spaceRoomType));
    }

    @PreAuthorize("@ss.hasPermi('space:roomType:edit')")
    @Log(title = "房间类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceRoomType spaceRoomType)
    {
        spaceRoomType.setUpdateBy(getUsername());
        return toAjax(spaceRoomTypeService.updateSpaceRoomType(spaceRoomType));
    }

    @PreAuthorize("@ss.hasPermi('space:roomType:remove')")
    @Log(title = "房间类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{typeIds}")
    public AjaxResult remove(@PathVariable Long[] typeIds)
    {
        return toAjax(spaceRoomTypeService.deleteSpaceRoomTypeByIds(typeIds));
    }
}
