package org.example.web.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.web.pojo.entity.EmpExpr;

import java.util.List;

public interface EmpExprMapper {

    @Select("select * from emp_expr where emp_id = #{empId} order by begin_date")
    List<EmpExpr> listByEmpId(Long empId);

    int batchInsert(@Param("list") List<EmpExpr> list);

    @Delete("delete from emp_expr where emp_id = #{empId}")
    int deleteByEmpId(Long empId);

    @Delete("<script>delete from emp_expr where emp_id in " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    int deleteByEmpIds(@Param("ids") List<Long> ids);
}
