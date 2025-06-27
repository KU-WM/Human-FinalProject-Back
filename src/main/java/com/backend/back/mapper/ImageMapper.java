package com.backend.back.mapper;

import com.backend.back.dto.ImageDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImageMapper {
    @Insert("INSERT INTO images(userInput, prompt, savePath, saveName, createBy) VALUES (#{userInput}, #{prompt}, #{savePath}, #{saveName}, #{createBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void saveImage(ImageDTO image);

    @Select("SELECT * FROM images ORDER BY id DESC")
    List<ImageDTO> getImages();

    @Select("Select * From images Where id = #{id}")
    ImageDTO getImage(int id);

    @Delete("Delete FROM images where id = #{id}")
    void deleteImage(int id);

    @Insert("INSERT INTO tempImages(userInput, prompt, savePath, saveName, tempUserUUID) VALUES (#{userInput}, #{prompt}, #{savePath}, #{saveName}, #{tempUserUUID})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void saveTempImage(ImageDTO tempImage);

    @Select("SELECT * FROM tempImages ORDER BY id DESC")
    List<ImageDTO> getTempImages();

    @Select("SELECT * FROM tempImages Where id = #{id}")
    ImageDTO getTempImage(int id);

    @Select("Select * from images where createBy = #{id} Order by id DESC")
    List<ImageDTO>  getUserImages(int id);

    @Select("Select * from images where createBy = #{id} limit #{offset}, #{pageSize}")
    List<ImageDTO>  getUserPagingImages(int id, int offset, int pageSize);

    @Select("Select count(*) From images Where createBy = #{id}")
    Integer getUserImageCount(int id);

    @Select("SELECT * FROM tempImages where tempUserUUID = #{userUUID} ORDER BY id DESC")
    List<ImageDTO> getTempUserImages(String userUUID);

    @Delete("Delete FROM tempImages where tempUserUUID = #{tempUserUUID}")
    void deleteTempImage(String tempUserUUID);

    @Delete("Delete * FROM tempImages where id > 0")
    void deleteAllTempImage();

    @Delete("Delete FROM tempImages where id = #{id}")
    void deleteTempImageFromId(int id);
}
