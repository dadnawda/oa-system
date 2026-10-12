package org.example.web.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 员工表 emp（deptName 为联查展示字段；exprList 仅详情接口填充）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emp {

    private Long id;
    private String name;
    private Integer gender;
    private String phone;
    private String job;
    private String image;
    private LocalDate entryDate;
    /** 在职状态: 1-在职 / 0-离职(留档不显示) */
    private Integer status;
    private Long deptId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 联查字段：部门名称 */
    private String deptName;

    /** 详情字段：工作经历列表 */
    private List<EmpExpr> exprList;
}
