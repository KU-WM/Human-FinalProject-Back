package com.backend.back.mapper;

import com.backend.back.dto.UserDTO;
import com.backend.back.dto.UuidToUserDTO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("select * from users where userId = #{userId}")
    UserDTO findByUserId(String userId);

    @Select("select userId from users where id = #{id}")
    String findById(int id);

    @Insert("Insert into users(userId, nickName, userPw) Values(#{userId}, #{nickName}, #{userPw})")
    void register(UserDTO user);

    @Update("Update users Set lastLogin = now() where userId = #{userId}")
    void lastLogin(String userId);

    @Insert("Insert into uuidToUser(uuid) value(#{uuid})")
    void uuidSetting(String uuid);

    @Update("Update uuidToUser Set userId = #{id} where uuid = #{uuid}")
    void uuidMatching(int id, String uuid);

    @Select("Select * From uuidToUser where uuid = #{uuid}")
    UuidToUserDTO findUuid(String uuid);

    @Update("update users set userGrade = #{grade} where id = #{id}")
    void updateUserGrade(int grade, int id);

    @Delete("Delete FROM users where id = #{id}")
    void deleteUser(int id);
}
