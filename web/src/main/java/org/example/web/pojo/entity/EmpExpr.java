package org.example.web.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 工作经历表 emp_expr
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpExpr {

    private Long id;
    private Long empId;
    private LocalDate beginDate;
    private LocalDate endDate;
    private Long deptId;
    private String job;
}
