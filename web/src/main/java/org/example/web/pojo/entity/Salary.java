package org.example.web.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工资表 salary（empName 为联查展示字段）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salary {

    private Long id;
    private Long empId;
    private String salaryMonth;
    private Double baseSalary;
    private Double bonus;
    private Double deduction;
    private Double actualPay;
    private LocalDate payDate;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 联查字段：员工姓名 */
    private String empName;
}
