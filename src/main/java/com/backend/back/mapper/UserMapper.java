package com.backend.back.mapper;

import com.backend.back.dto.ImageDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO temp(message) VALUES (#{message});")
    void testmessage(String message);

    @Insert("INSERT INTO image_data(userInput, prompt, image) VALUES (#{userInput}, #{prompt}, #{image})")
    void saveImage(ImageDTO image);

    @Select("SELECT * FROM image_data ORDER BY id DESC")
    List<ImageDTO> getImages();
}
