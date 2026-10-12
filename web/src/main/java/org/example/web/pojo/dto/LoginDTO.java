package org.example.web.pojo.dto;

import lombok.Data;

/**
 * 登录请求体：员工工号 + 密码
 */
@Data
public class LoginDTO {

    private Long empId;
    private String password;
}
