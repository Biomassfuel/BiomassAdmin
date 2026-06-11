package com.ruoyi.quartz.task;

import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.service.space.ISpaceReservationService;

/**
 * 空间预约定时任务
 */
@Component("spaceReservationTask")
public class SpaceReservationTask
{
    public void refreshFinishedReservations()
    {
        SpringUtils.getBean(ISpaceReservationService.class).refreshFinishedReservations();
    }
}
