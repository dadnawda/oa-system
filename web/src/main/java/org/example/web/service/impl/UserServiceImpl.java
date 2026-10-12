package org.example.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web.common.BusinessException;
import org.example.web.common.LoginUser;
import org.example.web.mapper.DeptMapper;
import org.example.web.mapper.EmpMapper;
import org.example.web.mapper.UserMapper;
import org.example.web.pojo.dto.LoginDTO;
import org.example.web.pojo.dto.RegisterDTO;
import org.example.web.pojo.entity.Dept;
import org.example.web.pojo.entity.Emp;
import org.example.web.pojo.entity.User;
import org.example.web.service.UserService;
import org.example.web.utils.JwtUtils;
import org.example.web.utils.PasswordUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final EmpMapper empMapper;
    private final DeptMapper deptMapper;

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        if (dto.getEmpId() == null || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BusinessException("工号与密码不能为空");
        }
        User user = userMapper.getByEmpId(dto.getEmpId());
        if (user == null) {
            throw new BusinessException("工号不存在，请先联系管理员先录入员工信息");
        }
        // 离职员工禁止登录（档案保留）
        Emp emp = empMapper.getById(user.getEmpId());
        if (emp != null && emp.getStatus() != null && emp.getStatus() == 0) {
            throw new BusinessException("该员工已离职，账户已停用");
        }
        if (!PasswordUtils.md5(dto.getPassword()).equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        // ===== 登录身份校验：身份即界面视角 =====
        String identity = (dto.getRole() == null || dto.getRole().isBlank()) ? user.getRole() : dto.getRole().trim();
        if (!"admin".equals(identity) && !"staff".equals(identity)) {
            throw new BusinessException("登录身份不合法，请选择管理员或员工");
        }
        if ("admin".equals(identity) && !"admin".equals(user.getRole())) {
            throw new BusinessException("该账号不是管理员，请选择「员工」身份登录");
        }
        String name = empMapper.selectNameById(user.getEmpId());
        String token = JwtUtils.generateToken(user.getEmpId(), user.getRole());
        log.info("员工工号 {} 登录成功，真实角色 {}，登录身份 {}", user.getEmpId(), user.getRole(), identity);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("empId", user.getEmpId());
        data.put("role", user.getRole());
        data.put("identity", identity);
        data.put("name", name);
        return data;
    }

    @Override
    public void register(RegisterDTO dto) {
        if (dto.getEmpId() == null) {
            throw new BusinessException("工号不能为空");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BusinessException("姓名不能为空");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new BusinessException("密码长度不能少于 6 位");
        }
        // 身份核验：必须是登记在册的员工，且姓名一致
        Emp emp = empMapper.getById(dto.getEmpId());
        if (emp == null) {
            throw new BusinessException("工号不存在，请联系管理员先录入员工信息");
        }
        if (emp.getStatus() != null && emp.getStatus() == 0) {
            throw new BusinessException("该员工已离职，无法注册");
        }
        if (!emp.getName().equals(dto.getName().trim())) {
            throw new BusinessException("姓名与该工号登记信息不一致");
        }
        // 一名员工仅一个账户：已注册则引导直接登录
        if (userMapper.getByEmpId(dto.getEmpId()) != null) {
            throw new BusinessException("该工号已注册过账号，请直接登录");
        }
        User user = new User();
        user.setEmpId(dto.getEmpId());
        user.setPassword(PasswordUtils.md5(dto.getPassword()));
        user.setRole("staff");
        userMapper.insert(user);
        log.info("员工 {}（{}）首次注册账号成功", dto.getEmpId(), emp.getName());
    }

    @Override
    public Map<String, Object> info(LoginUser loginUser) {
        Emp emp = empMapper.getById(loginUser.getEmpId());
        if (emp == null) {
            throw new BusinessException("员工信息不存在");
        }
        Dept dept = null;
        if (emp.getDeptId() != null && deptMapper.countById(emp.getDeptId()) > 0) {
            dept = deptMapper.findAll().stream()
                    .filter(d -> d.getId().equals(emp.getDeptId())).findFirst().orElse(null);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("empId", loginUser.getEmpId());
        data.put("role", loginUser.getRole());
        data.put("name", emp.getName());
        data.put("image", emp.getImage());
        data.put("phone", emp.getPhone());
        data.put("job", emp.getJob());
        data.put("entryDate", emp.getEntryDate());
        data.put("deptId", emp.getDeptId());
        data.put("deptName", dept != null ? dept.getName() : null);
        return data;
    }
}
