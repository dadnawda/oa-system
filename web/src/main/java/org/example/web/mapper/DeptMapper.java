package org.example.web.mapper;

import org.apache.ibatis.annotations.*;

import org.example.web.pojo.entity.Dept;

import java.util.List;

/**
 * 部门 Mapper：删除为逻辑删除（deleted=1 留档），查询均过滤已删除部门
 */
public interface DeptMapper {

    @Select("select * from dept where deleted = 0 order by id")
    List<Dept> findAll();

    @Select("select count(*) from dept where name = #{name} and deleted = 0")
    int countByName(String name);

    @Select("select count(*) from dept where name = #{name} and id != #{id} and deleted = 0")
    int countByNameExcludeId(@Param("name") String name, @Param("id") Long id);

    @Insert("insert into dept (name, description) values (#{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Dept dept);

    @Update("update dept set name = #{name}, description = #{description} where id = #{id} and deleted = 0")
    int update(Dept dept);

    /** 逻辑删除：留档不显示 */
    @Update("update dept set deleted = 1 where id = #{id}")
    int deleteById(Long id);

    @Select("select count(*) from emp where dept_id = #{deptId} and status = 1")
    int countEmpByDeptId(Long deptId);

    @Select("select count(*) from dept where id = #{id} and deleted = 0")
    int countById(Long id);
}
