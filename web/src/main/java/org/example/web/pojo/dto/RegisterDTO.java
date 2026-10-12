package org.example.web.pojo.dto;

import lombok.Data;

/**
 * 注册请求体：员工首次使用时凭 工号+姓名 验证身份并设置密码
 */
@Data
public class RegisterDTO {

    private Long empId;
    /** 姓名校验：必须与员工表登记姓名一致 */
    private String name;
    private String password;
}
