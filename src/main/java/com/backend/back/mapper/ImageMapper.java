package com.backend.back.mapper;

import com.backend.back.dto.ImageDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ImageMapper {
    @Insert("INSERT INTO images(userInput, prompt, savePath, saveName) VALUES (#{userInput}, #{prompt}, #{savePath}, #{saveName})")
    void saveImage(ImageDTO image);

    @Select("SELECT * FROM images ORDER BY id DESC")
    List<ImageDTO> getImages();
}
