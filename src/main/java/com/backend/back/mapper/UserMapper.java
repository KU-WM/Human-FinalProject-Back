package com.backend.back.mapper;

import com.backend.back.dto.ImageDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.json.simple.JSONObject;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO temp(message) VALUES (#{message});")
    void testmessage(String message);

    @Insert("INSERT INTO image_data(userInput, prompt, image) VALUES (#{userInput}, #{prompt}, #{image})")
    void saveImage(ImageDTO image);

    @Select("SELECT * FROM image_data ORDER BY id DESC")
    List<ImageDTO> getImages();

    @Select("SELECT dialogues FROM chat_data WHERE id = #{id}")
    String loadDialogue(int id);

    @Insert("INSERT INTO chat_data(dialogues, mine_type) VALUES (#{dialogue}, 'application/json')")
    void saveDialogue(String dialogue);

    @Update("UPDATE chat_data SET dialogues = JSON_ARRAY_APPEND(dialogues, '$.dialogues', JSON_OBJECT('user', #{userInput}, 'model', #{modelOutput})) WHERE id = 1")
    void updateDialogue(String userInput, String modelOutput);
}
