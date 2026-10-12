package org.example.web.service;

import org.example.web.common.LoginUser;
import org.example.web.pojo.dto.LoginDTO;
import org.example.web.pojo.dto.RegisterDTO;

import java.util.Map;

public interface UserService {

    /** 登录：校验工号与密码，返回 token/role/name/empId */
    Map<String, Object> login(LoginDTO dto);

    /** 注册：员工首次使用，凭 工号+姓名 验证身份并设置密码 */
    void register(RegisterDTO dto);

    /** 当前登录用户信息（联查 emp 姓名与部门） */
    Map<String, Object> info(LoginUser loginUser);
}
