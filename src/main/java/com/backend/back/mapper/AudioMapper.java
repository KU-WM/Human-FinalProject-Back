package com.backend.back.mapper;

import com.backend.back.dto.AudioDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AudioMapper {
    @Select("Select * from audios Order by id DESC")
    List<AudioDTO> getAudios();

    @Insert("INSERT INTO audios(userInput, prompt, savePath, saveName) VALUES (#{userInput}, #{prompt}, #{savePath}, #{saveName})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void saveAudio(AudioDTO audioDTO);
}
