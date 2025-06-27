package com.backend.back.controller;

import com.backend.back.dto.*;
import com.backend.back.mapper.AdminMapper;
import com.backend.back.mapper.ImageMapper;
import com.backend.back.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/page")
public class PageController {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ImageMapper imageMapper;

    @PostMapping("/paging")
    public Map<String, Object> PageList(@RequestBody HashMap<String, String> message) {
        
        PageDTO pageDTO = new PageDTO();
        if(message.get("page") != null) {
            pageDTO.setPage(Integer.parseInt(message.get("page")));
        }
        pageDTO.setPageFrom(message.get("pageFrom"));

        switch (pageDTO.getPageFrom()) {
            case "logs" -> {
                int logLen = adminMapper.getLogsLen();
                pageDTO.setPageLen((int) Math.ceil((double) logLen / pageDTO.getPageSize()));

                int reverseOffset = Math.max(logLen - pageDTO.getPage() * 30 + 1, 0);
                List<LogDTO> logs = adminMapper.getLogs(reverseOffset, reverseOffset == 0 ? logLen % 30 : 30);

                for(LogDTO log : logs) {
                    Integer id = adminMapper.getUserFromUuid(log.getUuid());
                    if(id != null) {
                        log.setUuid(userMapper.findById(id));
                    }
                }

                return Map.of(
                        "message", "pageReturn",
                        "list", logs,
                        "pageInfo", pageDTO
                );
            }
            case "statistics" -> {


                return Map.of(
                        "message", "pageReturn",
                        "list", "",
                        "pageInfo", pageDTO
                );
            }
            case "users" -> {
                List<UserDTO> users = adminMapper.getUsers(pageDTO.getOffset(), pageDTO.getPageSize());
                pageDTO.setPageLen((int) Math.ceil((double) adminMapper.getUsersLen() / pageDTO.getPageSize()));

                for(UserDTO user : users) {
                    user.setUserPw("");
                }

                return Map.of(
                        "message", "pageReturn",
                        "list", users,
                        "pageInfo", pageDTO
                );
            }
            case "userLogs" -> {
                if (message.get("userId") != null) {
                    int userId = Integer.parseInt(message.get("userId"));

                    int logLen = adminMapper.getUsersLogsLen(userId);
                    pageDTO.setPageLen((int) Math.ceil((double) logLen / pageDTO.getPageSize()));

                    int reverseOffset = Math.max(logLen - pageDTO.getPage() * 30, 0);
                    List<LogDTO> logs = adminMapper.getUsersLogs(userId, reverseOffset, reverseOffset == 0 ? logLen % 30 : 30);

                    for(LogDTO log : logs) {
                        log.setUuid(userMapper.findById(userId));
                    }

                    return Map.of(
                            "message", "pageReturn",
                            "list", logs,
                            "pageInfo", pageDTO
                    );
                } else {
                    return Map.of(
                            "message", "WrongInput"
                    );
                }
            }
            case "userImages" -> {
                if (message.get("userId") != null) {
                    int userId = Integer.parseInt(message.get("userId"));

                    int imageLen = imageMapper.getUserImageCount(userId);

                    int reverseOffset = Math.max(imageLen - pageDTO.getPage() * 8, 0);

                    pageDTO.setPageSize(8);
                    pageDTO.setPageLen((int) Math.ceil((double) imageLen / pageDTO.getPageSize()));

                    List<ImageDTO> images = imageMapper.getUserPagingImages(userId, reverseOffset, reverseOffset == 0 ? imageLen % 8 : 8);

                    return Map.of(
                            "message", "pageReturn",
                            "list", images,
                            "pageInfo", pageDTO
                    );
                } else {
                    return Map.of(
                            "message", "WrongInput"
                    );
                }
            }
            case "images" -> {
                List<ImageDTO> images = adminMapper.getImages(pageDTO.getOffset(), pageDTO.getPageSize());
                pageDTO.setPageLen((int) Math.ceil((double) adminMapper.getImagesLen() / pageDTO.getPageSize()));

                return Map.of(
                        "message", "pageReturn",
                        "list", images,
                        "pageInfo", pageDTO
                );
            }
            case "tempImages" -> {
                List<ImageDTO> tempImages = adminMapper.getTempImages(pageDTO.getOffset(), pageDTO.getPageSize());
                pageDTO.setPageLen((int) Math.ceil((double) adminMapper.getTempImagesLen() / pageDTO.getPageSize()));

                return Map.of(
                        "message", "pageReturn",
                        "list", tempImages,
                        "pageInfo", pageDTO
                );
            }
            default -> {
                return Map.of(
                        "message", "WrongInput"
                );
            }
        }
    }
}
