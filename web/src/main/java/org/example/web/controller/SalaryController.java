package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.common.Result;
import org.example.web.pojo.dto.SalaryQueryParam;
import org.example.web.pojo.entity.Salary;
import org.example.web.service.SalaryService;
import org.springframework.web.bind.annotation.*;

/**
 * 工资模块：员工查自己，管理员管理与发放
 */
@RestController
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    @GetMapping("/api/salaries/mine")
    public Result<?> mine() {
        return Result.success(salaryService.listMine());
    }

    @GetMapping("/api/admin/salaries")
    public Result<?> page(SalaryQueryParam param) {
        return Result.success(salaryService.page(param));
    }

    @PostMapping("/api/admin/salaries")
    public Result<Void> add(@RequestBody Salary salary) {
        salaryService.add(salary);
        return Result.success();
    }

    @PutMapping("/api/admin/salaries/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Salary salary) {
        salary.setId(id);
        salaryService.update(salary);
        return Result.success();
    }

    @PostMapping("/api/admin/salaries/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        salaryService.pay(id);
        return Result.success();
    }
}
