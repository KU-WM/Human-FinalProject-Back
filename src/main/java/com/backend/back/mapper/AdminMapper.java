package com.backend.back.mapper;

import com.backend.back.dto.AudioDTO;
import com.backend.back.dto.ImageDTO;
import com.backend.back.dto.LogDTO;
import com.backend.back.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper {
    @Select("Select * from accesslog where id >= #{offset} Limit #{pageSize}")
    List<LogDTO> getLogs(int offset, int pageSize);

    @Select("Select * from users where id >= #{offset} Limit #{pageSize}")
    List<UserDTO> getUsers(int offset, int pageSize);

    @Select("Select * from images where id >= #{offset} Limit #{pageSize}")
    List<ImageDTO> getImages(int offset, int pageSize);

    @Select("Select * from tempimages where id >= #{offset} Limit #{pageSize}")
    List<ImageDTO> getTempImages(int offset, int pageSize);

    @Select("Select count(*) from accesslog")
    int getLogsLen();

    @Select("Select count(*) from users")
    int getUsersLen();

    @Select("Select count(*) from images")
    int getImagesLen();

    @Select("Select count(*) from tempimages")
    int getTempImagesLen();

    @Select("select userId from uuidtouser where uuid = #{uuid}")
    Integer getUserFromUuid(String uuid);

    @Select("""
            select * from accesslog a
            \twhere exists
            \t(select 1 from (
            \t\tselect u.uuid from uuidtouser u
            \t\t\twhere u.userid = #{id}
            \t\t) sq
                    where sq.uuid = a.uuid
                )
                Limit #{offset}, #{pageSize}""")
    List<LogDTO> getUsersLogs(int id, int offset, int pageSize);

    @Select("""
            select count(*) from accesslog a
            \twhere exists
            \t(select 1 from (
            \t\tselect u.uuid from uuidtouser u 
            \t\t\twhere u.userid = #{id}
            \t\t) sq
                    where sq.uuid = a.uuid
                )""")
    int getUsersLogsLen(int id);
}
