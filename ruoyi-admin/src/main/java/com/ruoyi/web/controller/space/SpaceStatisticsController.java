package com.ruoyi.web.controller.space;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.space.SpaceRoomStatistics;
import com.ruoyi.system.domain.space.SpaceStatisticsQuery;
import com.ruoyi.system.service.space.ISpaceStatisticsService;

@RestController
@RequestMapping("/space/statistics")
public class SpaceStatisticsController extends BaseController
{
    @Autowired
    private ISpaceStatisticsService spaceStatisticsService;

    @PreAuthorize("@ss.hasPermi('space:statistics:list')")
    @GetMapping("/dashboard")
    public AjaxResult dashboard(SpaceStatisticsQuery query)
    {
        return success(spaceStatisticsService.selectDashboard(query));
    }

    @PreAuthorize("@ss.hasPermi('space:statistics:export')")
    @Log(title = "房间预约数据统计", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SpaceStatisticsQuery query)
    {
        List<SpaceRoomStatistics> list = spaceStatisticsService.selectRoomStatistics(query);
        ExcelUtil<SpaceRoomStatistics> util = new ExcelUtil<SpaceRoomStatistics>(SpaceRoomStatistics.class);
        util.exportExcel(response, list, "房间预约数据统计");
    }
}
