package com.backend.back.controller;

import com.backend.back.mapper.StatisticMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistic")
public class StatisticController {

    @Autowired
    private StatisticMapper statisticMapper;

    @GetMapping("/kpi")
    public Map<String, Integer> getKpi() {
        int visitors = statisticMapper.getUniqueAccessUser();
        int loginUsers = statisticMapper.getLoginAccessUser();

        int loginRate = Math.round((float) loginUsers / visitors * 100);

        Map<String, Object> requestInfo = statisticMapper.getRequestAndBytesSent();
        int totalRequest = Integer.parseInt(requestInfo.get("request").toString());
        int totalBytes = Math.round((float) (Long.parseLong(requestInfo.get("bytesSent").toString()) * 100) / 1024 / 1024 / 1024);


        return Map.of(
                "visitors", visitors,
                "logInUsers", loginUsers,
                "logInRate", loginRate,
                "totalRequests", totalRequest,
                "totalBytes", totalBytes,
                "totalErrors", statisticMapper.getErrorCount()
        );
    }

    @GetMapping("/statusCodeData")
    public List<Map<String, Object>> getStatusCodeData() {
        return statisticMapper.getTop3Error();
    }

    @GetMapping("/errorPathData")
    public List<Map<String, Object>> getErrorPathData() {
        return statisticMapper.getTop3ErrorOccurLocation();
    }

    @GetMapping("/hourlyRequestDataByDay")
    public List<Object> getHourData() {
        List<Object> hourData = new ArrayList<>();

        for(int i = 0; i < 24; i++) {
            int today = statisticMapper.getLogInOneHour(i + 1, i);
            int ondDayAgo = statisticMapper.getLogInOneHour(i + 1 + 24, i + 24);
            int twoDayAgo = statisticMapper.getLogInOneHour(i + 1 + 48, i + 48);
            hourData.add(Map.of(
                    "hour", i,
                    "today", today,
                    "oneDayAgo", ondDayAgo,
                    "twoDayAgo", twoDayAgo
            ));
        }

        return hourData;
    }

    @GetMapping("/byteSentPerHour")
    public List<Object> getBytePerHour() {
        List<Object> byteData = new ArrayList<>();

        for(int i = 0; i < 24; i++) {
            float bytes = (float) (Math.round(statisticMapper.getByteSentPerHour(i + 1, i) * 100 / 1024) * 0.01);
            byteData.add(Map.of(
                    "hour", i,
                    "KB", bytes
            ));
        }

        return byteData;
    }
}
