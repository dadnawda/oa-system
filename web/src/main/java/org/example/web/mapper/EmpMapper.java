package org.example.web.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.web.pojo.dto.EmpQueryParam;
import org.example.web.pojo.entity.Emp;

import java.util.List;

/**
 * 员工 Mapper：删除为逻辑删除（status=0 离职留档），分页查询默认隐藏离职人员
 */
public interface EmpMapper {

    List<Emp> page(EmpQueryParam param);

    Long count(EmpQueryParam param);

    Emp getById(Long id);

    /** 显式插入自动生成的工号 */
    int insert(Emp emp);

    int update(Emp emp);

    /** 批量标记离职（逻辑删除，留档） */
    int markResigned(@Param("ids") List<Long> ids);

    @Select("select name from emp where id = #{id}")
    String selectNameById(Long id);

    @Select("select count(*) from emp where id = #{id}")
    int existsById(Long id);

    @Select("select count(*) from emp where dept_id = #{deptId} and status = 1")
    int countActiveByDept(Long deptId);

    @Update("update emp set job = #{job} where id = #{id}")
    int updateJob(@Param("id") Long id, @Param("job") String job);

    @Update("update emp set job = #{job}, dept_id = #{deptId} where id = #{id}")
    int updateJobAndDept(@Param("id") Long id, @Param("job") String job, @Param("deptId") Long deptId);
}
