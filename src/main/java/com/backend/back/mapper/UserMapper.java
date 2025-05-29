package com.backend.back.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO temp(message) VALUES (#{message});")
    void testmessage(String message);
}
