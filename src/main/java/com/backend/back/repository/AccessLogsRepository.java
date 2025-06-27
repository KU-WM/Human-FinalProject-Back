package com.backend.back.repository;

import com.backend.back.dto.AccessLogDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.util.List;

@Repository
public class AccessLogsRepository {

    private final JdbcTemplate jdbc;
    private final int BATCH_SIZE = 500;
    private final int[] ARG_TYPES = new int[]{
            Types.VARCHAR,      // clientIp
            Types.TIMESTAMP,    // accessTime
            Types.VARCHAR,      // requestMethod
            Types.VARCHAR,      // requestLocation
            Types.INTEGER,      // uuid
            Types.INTEGER,      // statusCode
            Types.VARCHAR       // responseBytes
    };

    public AccessLogsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public void insertLogs(List<AccessLogDTO> logs) {
        String sql = "INSERT INTO accesslog (clientIp, accessTime, requestMethod, requestLocation, statusCode, bytesSent, uuid) VALUES (?, ?, ?, ?, ?, ?, ?)";

        for(int i = 0; i < logs.size(); i += BATCH_SIZE) {
            List<AccessLogDTO> chunk = logs.subList(i, Math.min(i + BATCH_SIZE,logs.size()));
            List<Object[]> batchArgs = chunk.stream()
                    .map(log -> new Object[]{
                            log.getClientIp(),
                            log.getAccessTime(),
                            log.getRequestMethod(),
                            log.getRequestLocation(),
                            log.getStatusCode(),
                            log.getResponseBytes(),
                            log.getUuid()
                    })
                    .toList();

            jdbc.batchUpdate(sql, batchArgs, ARG_TYPES);

        }
    }
}
