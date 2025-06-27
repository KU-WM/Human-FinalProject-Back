package com.backend.back.mapper;

import com.backend.back.dto.AudioDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AudioMapper {
    @Select("Select * from audios Order by id DESC")
    List<AudioDTO> getAudios();

    @Insert("INSERT INTO audios(userInput, prompt, savePath, saveName, createBy, createFrom) VALUES (#{userInput}, #{prompt}, #{savePath}, #{saveName}, #{createBy}, #{createFrom})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void saveAudio(AudioDTO audioDTO);

    @Select("Select * From audios Where id = #{id}")
    AudioDTO getAudio(int id);

    @Delete("Delete FROM audios where id = #{id}")
    void deleteAudio(int id);

    @Select("Select * from audios where createFrom = #{createFrom} Order by id DESC")
    List<AudioDTO> getImageAudios(int createFrom);

    @Select("SELECT * FROM tempAudios where tempUserUUID = #{userUUID} and createFrom = #{id} ORDER BY id DESC")
    List<AudioDTO> getTempUserAudios(String userUUID, int id);

    @Insert("INSERT INTO tempAudios(userInput, prompt, savePath, saveName, tempUserUUID, createFrom) VALUES (#{userInput}, #{prompt}, #{savePath}, #{saveName}, #{tempUserUUID}, #{createFrom})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void saveTempAudio(AudioDTO tempAudioDTO);

    @Delete("Delete FROM tempAudios where tempUserUUID = #{tempUserUUID}")
    void deleteTempAudio(String tempUserUUID);

    @Delete("Delete FROM tempAudios where id > 0")
    void deleteAllTempAudio();

    @Select("Select * from tempAudios where createFrom = #{createFrom} Order by id DESC")
    List<AudioDTO> getTempImageAudios(int createFrom);

    @Delete("Delete FROM tempAudios where id = #{id}")
    void deleteTempAudioFromId(int id);

    @Select("Select * From tempAudios Where id = #{id}")
    AudioDTO getTempAudio(int id);
}
