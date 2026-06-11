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
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.space.SpaceRoom;
import com.ruoyi.system.domain.space.SpaceRoomEquipment;
import com.ruoyi.system.service.space.ISpaceReservationService;
import com.ruoyi.system.service.space.ISpaceRoomService;

@RestController
@RequestMapping("/space/room")
public class SpaceRoomController extends BaseController
{
    @Autowired
    private ISpaceRoomService spaceRoomService;

    @Autowired
    private ISpaceReservationService spaceReservationService;

    @PreAuthorize("@ss.hasPermi('space:room:list')")
    @GetMapping("/list")
    public TableDataInfo list(SpaceRoom spaceRoom)
    {
        startPage();
        List<SpaceRoom> list = spaceRoomService.selectSpaceRoomList(spaceRoom);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:reservationItem:publicList')")
    @GetMapping("/public/list")
    public TableDataInfo publicList(SpaceRoom spaceRoom)
    {
        startPage();
        List<SpaceRoom> list = spaceRoomService.selectSpaceRoomList(spaceRoom);
        list.forEach(this::sanitizePublicRoom);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:reservationItem:publicList')")
    @GetMapping("/approved-reservation/list")
    public TableDataInfo approvedReservationRoomList(SpaceRoom spaceRoom)
    {
        spaceRoom.setApprovedReservationOnly(true);
        spaceReservationService.refreshFinishedReservations();
        startPage();
        List<SpaceRoom> list = spaceRoomService.selectSpaceRoomList(spaceRoom);
        list.forEach(this::sanitizePublicRoom);
        return getDataTable(list);
    }

    private void sanitizePublicRoom(SpaceRoom room)
    {
        if (room == null)
        {
            return;
        }
        room.setAssignedOrgId(null);
        room.setAssignedOrgName(null);
        room.setDelFlag(null);
        room.setCreateBy(null);
        room.setUpdateBy(null);
        room.setRemark(null);
        if (room.getRoomEquipmentList() != null)
        {
            room.getRoomEquipmentList().forEach(this::sanitizePublicRoomEquipment);
        }
    }

    private void sanitizePublicRoomEquipment(SpaceRoomEquipment equipment)
    {
        if (equipment == null)
        {
            return;
        }
        equipment.setCreateBy(null);
        equipment.setUpdateBy(null);
        equipment.setRemark(null);
    }

    @PreAuthorize("@ss.hasPermi('space:room:list')")
    @GetMapping("/recycle/list")
    public TableDataInfo recycleList(SpaceRoom spaceRoom)
    {
        startPage();
        List<SpaceRoom> list = spaceRoomService.selectDeletedSpaceRoomList(spaceRoom);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('space:room:export')")
    @Log(title = "房间", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SpaceRoom spaceRoom)
    {
        List<SpaceRoom> list = spaceRoomService.selectSpaceRoomList(spaceRoom);
        ExcelUtil<SpaceRoom> util = new ExcelUtil<SpaceRoom>(SpaceRoom.class);
        util.exportExcel(response, list, "房间数据");
    }

    @PreAuthorize("@ss.hasPermi('space:room:query')")
    @GetMapping(value = "/{roomId}")
    public AjaxResult getInfo(@PathVariable Long roomId)
    {
        return success(spaceRoomService.selectSpaceRoomById(roomId));
    }

    @PreAuthorize("@ss.hasPermi('space:reservationItem:publicList')")
    @GetMapping(value = "/public/{roomId}")
    public AjaxResult publicInfo(@PathVariable Long roomId)
    {
        SpaceRoom room = spaceRoomService.selectSpaceRoomById(roomId);
        sanitizePublicRoom(room);
        return success(room);
    }

    @PreAuthorize("@ss.hasPermi('space:room:add')")
    @Log(title = "房间", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SpaceRoom spaceRoom)
    {
        spaceRoom.setCreateBy(getUsername());
        return toAjax(spaceRoomService.insertSpaceRoom(spaceRoom));
    }

    @PreAuthorize("@ss.hasPermi('space:room:import')")
    @Log(title = "房间导入", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SpaceRoom> util = new ExcelUtil<SpaceRoom>(SpaceRoom.class);
        List<SpaceRoom> roomList = util.importExcel(file.getInputStream());
        String message = spaceRoomService.importRoom(roomList, updateSupport, getUsername(), file.getOriginalFilename());
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<SpaceRoom> util = new ExcelUtil<SpaceRoom>(SpaceRoom.class);
        util.importTemplateExcel(response, "房间数据");
    }

    @PreAuthorize("@ss.hasPermi('space:room:edit')")
    @Log(title = "房间", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SpaceRoom spaceRoom)
    {
        spaceRoom.setUpdateBy(getUsername());
        return toAjax(spaceRoomService.updateSpaceRoom(spaceRoom));
    }

    @PreAuthorize("@ss.hasPermi('space:room:remove')")
    @Log(title = "房间", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roomIds}")
    public AjaxResult remove(@PathVariable Long[] roomIds)
    {
        return toAjax(spaceRoomService.deleteSpaceRoomByIds(roomIds));
    }

    @PreAuthorize("@ss.hasPermi('space:room:edit')")
    @Log(title = "房间恢复", businessType = BusinessType.UPDATE)
    @PutMapping("/restore/{roomIds}")
    public AjaxResult restore(@PathVariable Long[] roomIds)
    {
        return toAjax(spaceRoomService.restoreSpaceRoomByIds(roomIds));
    }

    @PreAuthorize("@ss.hasPermi('space:room:remove')")
    @Log(title = "房间永久删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/force/{roomIds}")
    public AjaxResult forceRemove(@PathVariable Long[] roomIds)
    {
        return toAjax(spaceRoomService.forceDeleteSpaceRoomByIds(roomIds));
    }
}
