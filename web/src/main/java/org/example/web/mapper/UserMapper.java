package org.example.web.mapper;

import org.apache.ibatis.annotations.*;
import org.example.web.pojo.entity.User;

import java.util.List;

public interface UserMapper {

    @Select("select * from user where emp_id = #{empId}")
    User getByEmpId(Long empId);

    @Insert("insert into user (emp_id, password, role) values (#{empId}, #{password}, #{role})")
    int insert(User user);

    @Update("update user set password = #{password} where emp_id = #{empId}")
    int updatePassword(@Param("empId") Long empId, @Param("password") String password);

    @Delete("delete from user where emp_id = #{empId}")
    int deleteByEmpId(Long empId);
}
