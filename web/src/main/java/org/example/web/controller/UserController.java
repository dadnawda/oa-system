package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.common.LoginUser;
import org.example.web.common.Result;
import org.example.web.common.UserContext;
import org.example.web.pojo.dto.LoginDTO;
import org.example.web.pojo.dto.RegisterDTO;
import org.example.web.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户认证模块：登录 / 注册 / 当前用户信息
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    /** 员工首次注册：凭 工号+姓名 核验身份并设置密码 */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @GetMapping("/user/info")
    public Result<Map<String, Object>> info() {
        LoginUser loginUser = UserContext.get();
        return Result.success(userService.info(loginUser));
    }
}
