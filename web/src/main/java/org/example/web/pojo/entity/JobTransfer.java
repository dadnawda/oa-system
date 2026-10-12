package org.example.web.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 职位调整申请表 job_transfer（empName / approverName / targetDeptName 为联查展示字段）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobTransfer {

    private Long id;
    private Long empId;
    private String currentJob;
    private String targetJob;
    private Long targetDeptId;
    private String reason;
    private Integer status;
    private Long approverId;
    private LocalDateTime approveTime;
    private String approveRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 联查字段：申请员工姓名 */
    private String empName;
    /** 联查字段：审批人姓名 */
    private String approverName;
    /** 联查字段：目标部门名称 */
    private String targetDeptName;
}
