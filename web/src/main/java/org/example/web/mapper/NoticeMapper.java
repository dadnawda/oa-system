package org.example.web.mapper;

import org.apache.ibatis.annotations.*;
import org.example.web.pojo.entity.Notice;

import java.util.List;

public interface NoticeMapper {

    @Select("<script>select n.*, e.name as publisher_name from notice n " +
            "left join emp e on n.user_id = e.id " +
            "<where>" +
            "  <if test='status != null'>n.status = #{status}</if>" +
            "</where> " +
            "order by n.publish_time desc limit #{offset}, #{pageSize}</script>")
    List<Notice> page(@Param("status") Integer status, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("<script>select count(*) from notice <where>" +
            "<if test='status != null'>status = #{status}</if></where></script>")
    Long count(@Param("status") Integer status);

    @Select("select n.*, e.name as publisher_name from notice n left join emp e on n.user_id = e.id where n.id = #{id}")
    Notice getById(Long id);

    @Insert("insert into notice (title, content, user_id, status) values (#{title}, #{content}, #{userId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notice notice);

    @Update("<script>update notice " +
            "<set>" +
            "  <if test='title != null'>title = #{title},</if>" +
            "  <if test='content != null'>content = #{content},</if>" +
            "  <if test='status != null'>status = #{status},</if>" +
            "</set> where id = #{id}</script>")
    int update(Notice notice);

    @Delete("delete from notice where id = #{id}")
    int deleteById(Long id);
}
