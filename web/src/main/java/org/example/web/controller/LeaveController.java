package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.common.Result;
import org.example.web.pojo.dto.StatusPageParam;
import org.example.web.pojo.entity.LeaveRequest;
import org.example.web.service.LeaveService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 请假审批模块：员工提交/查看自己的，管理员审批
 */
@RestController
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping("/api/leaves/mine")
    public Result<List<LeaveRequest>> mine() {
        return Result.success(leaveService.listMine());
    }

    @PostMapping("/api/leaves")
    public Result<Void> submit(@RequestBody LeaveRequest leaveRequest) {
        leaveService.submit(leaveRequest);
        return Result.success();
    }

    @GetMapping("/api/admin/leaves")
    public Result<?> page(StatusPageParam param) {
        return Result.success(leaveService.page(param));
    }

    @PutMapping("/api/admin/leaves/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean approved = Boolean.TRUE.equals(body.get("approved"));
        String remark = body.get("remark") == null ? null : String.valueOf(body.get("remark"));
        leaveService.approve(id, approved, remark);
        return Result.success();
    }
}
