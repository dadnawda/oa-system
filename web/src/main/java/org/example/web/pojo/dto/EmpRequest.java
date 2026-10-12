package org.example.web.pojo.dto;

import lombok.Data;

/**
 * 员工新增/修改请求体（含工作经历）
 */
@Data
public class EmpRequest {

    private Long id;
    private String name;
    private Integer gender;
    private String phone;
    private String job;
    private String image;
    private String entryDate;
    private Long deptId;
    private java.util.List<org.example.web.pojo.entity.EmpExpr> exprList;
}
