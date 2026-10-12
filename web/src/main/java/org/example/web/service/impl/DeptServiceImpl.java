package org.example.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web.common.BusinessException;
import org.example.web.mapper.DeptMapper;
import org.example.web.pojo.entity.Dept;
import org.example.web.service.DeptService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;

    @Override
    public List<Dept> listAll() {
        return deptMapper.findAll();
    }

    @Override
    public void add(Dept dept) {
        if (dept.getName() == null || dept.getName().isBlank()) {
            throw new BusinessException("部门名称不能为空");
        }
        if (deptMapper.countByName(dept.getName()) > 0) {
            throw new BusinessException("部门名称已存在：" + dept.getName());
        }
        deptMapper.insert(dept);
        log.info("新增部门 {} -> id={}", dept.getName(), dept.getId());
    }

    @Override
    public void update(Dept dept) {
        if (dept.getId() == null || deptMapper.countById(dept.getId()) == 0) {
            throw new BusinessException("部门不存在");
        }
        if (dept.getName() == null || dept.getName().isBlank()) {
            throw new BusinessException("部门名称不能为空");
        }
        if (deptMapper.countByNameExcludeId(dept.getName(), dept.getId()) > 0) {
            throw new BusinessException("部门名称已存在：" + dept.getName());
        }
        deptMapper.update(dept);
    }

    @Override
    public void delete(Long id) {
        if (deptMapper.countById(id) == 0) {
            throw new BusinessException("部门不存在");
        }
        if (deptMapper.countEmpByDeptId(id) > 0) {
            throw new BusinessException("该部门下仍有员工，无法删除");
        }
        deptMapper.deleteById(id);
    }
}
