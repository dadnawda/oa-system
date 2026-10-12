package org.example.web.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假审批表 leave_request（empName / approverName 为联查展示字段）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {

    private Long id;
    private Long empId;
    private Integer leaveType;
    private LocalDate beginDate;
    private LocalDate endDate;
    private Double days;
    private String reason;
    private Integer status;
    private Long approverId;
    private LocalDateTime approveTime;
    private String approveRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 联查字段：请假员工姓名 */
    private String empName;
    /** 联查字段：审批人姓名 */
    private String approverName;
}
