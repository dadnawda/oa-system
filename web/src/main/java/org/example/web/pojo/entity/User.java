package org.example.web.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户表 user —— 主键即员工工号
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long empId;
    private String password;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
