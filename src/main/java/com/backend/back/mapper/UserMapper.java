package com.backend.back.mapper;

import com.backend.back.dto.UserDTO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("select * from users where userId = #{userId}")
    UserDTO findByUserId(String userId);

    @Insert("Insert into users(userId, nickName, userPw) Values(#{userId}, #{nickName}, #{userPw})")
    void register(UserDTO user);

    @Update("Update users Set lastLogin = now() where userId = #{userId}")
    void lastLogin(String userId);
}
