package org.example.web.pojo.dto;

import lombok.Data;

/**
 * 登录请求体：员工工号 + 密码 + 登录身份
 */
@Data
public class LoginDTO {

    private Long empId;
    private String password;
    /** 登录身份：admin-管理员 / staff-员工（为空时按账号真实角色） */
    private String role;
}
