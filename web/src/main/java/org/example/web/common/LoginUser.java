package org.example.web.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录用户（由拦截器解析 token 后写入 ThreadLocal）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    /** 员工工号（即 user 主键） */
    private Long empId;
    /** admin / staff */
    private String role;
    /** 员工姓名（联查 emp 得到，用于展示） */
    private String name;
}
