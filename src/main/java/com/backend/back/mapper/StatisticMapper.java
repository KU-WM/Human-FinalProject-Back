package com.backend.back.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticMapper {
    @Select("select count(distinct userId) + count(*) - count(userId) from uuidtouser")
    Integer getUniqueAccessUser();

    @Select("select count(distinct userId) from uuidtouser")
    Integer getLoginAccessUser();

    @Select("select count(*) as request, sum(bytesSent) as bytesSent from accesslog")
    Map<String, Object> getRequestAndBytesSent();

    @Select("select count(*) from accesslog where statusCode >= 400")
    Integer getErrorCount();

    @Select("select statusCode, count(*) as value from accesslog where statusCode >= 400 group by statusCode order by count(*) desc limit 5")
    List<Map<String, Object>> getTop3Error();

    @Select("select requestLocation, count(*) as value from accesslog where statusCode >= 400 group by requestLocation order by count(*) desc limit 5")
    List<Map<String, Object>> getTop3ErrorOccurLocation();

    @Select("""
            SELECT ifnull(count(*), 0)
            FROM accesslog
            WHERE accessTime >= DATE_ADD(now(), INTERVAL -#{time1} HOUR) and accessTime < DATE_ADD(now(), INTERVAL -#{time2} HOUR)""")
    Integer getLogInOneHour(int time1, int time2);

    @Select("""
            SELECT 	ifnull(avg(bytesSent), 0)
            FROM 	accesslog
            WHERE 	accessTime >= DATE_ADD(now(), INTERVAL -#{time1} HOUR) and accessTime < DATE_ADD(now(), INTERVAL -#{time2} HOUR)
            """)
    Float getByteSentPerHour(int time1, int time2);
}
