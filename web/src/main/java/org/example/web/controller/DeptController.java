package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.common.Result;
import org.example.web.pojo.entity.Dept;
import org.example.web.service.DeptService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理模块（管理员专用，/api/admin 路径前缀统一鉴权）
 */
@RestController
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @GetMapping("/api/depts")
    public Result<List<Dept>> list() {
        return Result.success(deptService.listAll());
    }

    @PostMapping("/api/admin/depts")
    public Result<Void> add(@RequestBody Dept dept) {
        deptService.add(dept);
        return Result.success();
    }

    @PutMapping("/api/admin/depts/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Dept dept) {
        dept.setId(id);
        deptService.update(dept);
        return Result.success();
    }

    @DeleteMapping("/api/admin/depts/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deptService.delete(id);
        return Result.success();
    }
}
