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
import com.ruoyi.system.domain.space.SpaceRoomEquipment;
import com.ruoyi.system.service.space.ISpaceRoomEquipmentService;

@RestController
@RequestMapping("/space/room/equipment")
public class SpaceRoomEquipmentController extends BaseController
{
    @Autowired
    private ISpaceRoomEquipmentService spaceRoomEquipmentService;

    @PreAuthorize("@ss.hasPermi('space:roomEquipment:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceRoomEquipment spaceRoomEquipment)
    {
        startPage();
        List<SpaceRoomEquipment> list = spaceRoomEquipmentService.selectSpaceRoomEquipmentList(spaceRoomEquipment);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:roomEquipment:query')")
    @GetMapping(value = "/{roomEquipmentId}")
    public AjaxResult getInfo(@PathVariable Long roomEquipmentId)
    {
        return success(spaceRoomEquipmentService.selectSpaceRoomEquipmentById(roomEquipmentId));
    }

    @PreAuthorize("@ss.hasPermi('space:roomEquipment:add')")
    @Log(title = "房间设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceRoomEquipment spaceRoomEquipment)
    {
        spaceRoomEquipment.setCreateBy(getUsername());
        return toAjax(spaceRoomEquipmentService.insertSpaceRoomEquipment(spaceRoomEquipment));
    }

    @PreAuthorize("@ss.hasPermi('space:roomEquipment:edit')")
    @Log(title = "房间设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceRoomEquipment spaceRoomEquipment)
    {
        spaceRoomEquipment.setUpdateBy(getUsername());
        return toAjax(spaceRoomEquipmentService.updateSpaceRoomEquipment(spaceRoomEquipment));
    }

    @PreAuthorize("@ss.hasPermi('space:roomEquipment:remove')")
    @Log(title = "房间设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roomEquipmentIds}")
    public AjaxResult remove(@PathVariable Long[] roomEquipmentIds)
    {
        return toAjax(spaceRoomEquipmentService.deleteSpaceRoomEquipmentByIds(roomEquipmentIds));
    }
}