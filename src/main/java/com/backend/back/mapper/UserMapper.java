package com.backend.back.mapper;

import com.backend.back.dto.ImageDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO temp(message) VALUES (#{message});")
    void testmessage(String message);

    @Insert("INSERT INTO image_data(userInput, prompt, image) VALUES (#{userInput}, #{prompt}, #{image})")
    void saveImage(ImageDTO image);
}
