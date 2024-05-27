package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @Author: chenyu.di
 * @since: 2024-05-27 16:41
 */
@RestController
@RequestMapping("meeting/statistics")
public class StatisticsController {

    @GetMapping("/location")
    public Result<Objects> getLocationTimes () {
        return null;
    }

    @GetMapping("/time-period")
    public Result<Objects> getTimePeriodTimes () {
        return null;
    }
}