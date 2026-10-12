package org.example.web.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.web.pojo.dto.SalaryQueryParam;
import org.example.web.pojo.entity.Salary;

import java.util.List;

/**
 * 工资 Mapper：分页为动态 SQL，见 resources/mapper/SalaryMapper.xml
 */
public interface SalaryMapper {

    List<Salary> page(SalaryQueryParam param);

    Long count(SalaryQueryParam param);

    List<Salary> listByEmpId(Long empId);

    Salary getById(Long id);

    int insert(Salary salary);

    int update(Salary salary);

    @Select("select count(*) from salary where emp_id = #{empId} and salary_month = #{salaryMonth}")
    int existsByEmpMonth(@Param("empId") Long empId, @Param("salaryMonth") String salaryMonth);

    @Update("update salary set status = 1, pay_date = #{payDate} where id = #{id} and status = 0")
    int markPaid(@Param("id") Long id, @Param("payDate") java.time.LocalDate payDate);
}
