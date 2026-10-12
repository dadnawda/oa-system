package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.common.Result;
import org.example.web.pojo.dto.EmpQueryParam;
import org.example.web.pojo.dto.EmpRequest;
import org.example.web.pojo.entity.Emp;
import org.example.web.service.EmpService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 员工信息管理模块（管理员专用）
 */
@RestController
@RequiredArgsConstructor
public class EmpController {

    private final EmpService empService;

    @GetMapping("/api/admin/emps")
    public Result<?> page(EmpQueryParam param) {
        return Result.success(empService.page(param));
    }

    @GetMapping("/api/admin/emps/{id}")
    public Result<Emp> detail(@PathVariable Long id) {
        return Result.success(empService.detail(id));
    }

    @PostMapping("/api/admin/emps")
    public Result<Void> add(@RequestBody EmpRequest req) {
        empService.add(req);
        return Result.success();
    }

    @PutMapping("/api/admin/emps/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody EmpRequest req) {
        req.setId(id);
        empService.update(req);
        return Result.success();
    }

    @DeleteMapping("/api/admin/emps")
    public Result<Void> deleteBatch(@RequestParam List<Long> ids) {
        empService.deleteBatch(ids);
        return Result.success();
    }

    /** 上传员工个人照片：multipart 表单，字段名 file */
    @PostMapping("/api/admin/emps/{id}/photo")
    public Result<String> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return Result.success(empService.uploadPhoto(id, file));
    }
}
