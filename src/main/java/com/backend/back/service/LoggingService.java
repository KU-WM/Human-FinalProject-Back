package com.backend.back.service;

import com.backend.back.dto.AccessLogDTO;
import com.backend.back.repository.AccessLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoggingService {

    @Autowired
    private AccessLogsRepository accessLogsRepository;

    public List<AccessLogDTO> GetLog() throws IOException {

        BufferedReader reader = new BufferedReader(
                new FileReader("C:\\Program Files\\nginx-1.27.5\\logs\\access.log")
        );

        List<AccessLogDTO> logs = new ArrayList<>();

        String str;
        while ((str = reader.readLine()) != null) {
            AccessLogDTO log = new AccessLogDTO();

//            System.out.println(str);
            String[] temp = str.split(" ");
            if(temp.length < 8) {
                System.out.println("\n\n\n" + str + "\n\n\n");
                continue;
            }
//            System.out.println(Arrays.toString(temp));
            log.setClientIp(temp[0]);

            String isoString = temp[3];
            OffsetDateTime odt = OffsetDateTime.parse(isoString);
            LocalDateTime ldt = odt.toLocalDateTime();
            log.setAccessTime(ldt);

            log.setRequestMethod(temp[4].split("\"")[1]);
            log.setRequestLocation(temp[5]);
            log.setStatusCode(Integer.parseInt(temp[7]));
            log.setResponseBytes(Integer.parseInt((temp[8])));

            String uuid;
            if((uuid = temp[9].split("=")[1]).equals("-")) {
                // 최초 접속시 or 봇 처리 - 봇 처리는 cloudflare 에서 차단 설정함
                log.setUuid("FirstLogin");
            }
            else {
                log.setUuid(uuid);
            }

            logs.add(log);
        }

        accessLogsRepository.insertLogs(logs);
        reader.close();

        FileWriter fw = new FileWriter("C:\\Program Files\\nginx-1.27.5\\logs\\access.log", false); // append = false
        fw.write("");  // 내용 삭제
        fw.close();

        return logs;
    }
}
