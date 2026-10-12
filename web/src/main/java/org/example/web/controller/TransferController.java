package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.common.Result;
import org.example.web.pojo.dto.StatusPageParam;
import org.example.web.pojo.entity.JobTransfer;
import org.example.web.service.TransferService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 职位调整审批模块：员工提交/查看自己的，管理员审批（通过后同步更新员工职位）
 */
@RestController
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @GetMapping("/api/transfers/mine")
    public Result<List<JobTransfer>> mine() {
        return Result.success(transferService.listMine());
    }

    @PostMapping("/api/transfers")
    public Result<Void> submit(@RequestBody JobTransfer jobTransfer) {
        transferService.submit(jobTransfer);
        return Result.success();
    }

    @GetMapping("/api/admin/transfers")
    public Result<?> page(StatusPageParam param) {
        return Result.success(transferService.page(param));
    }

    @PutMapping("/api/admin/transfers/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean approved = Boolean.TRUE.equals(body.get("approved"));
        String remark = body.get("remark") == null ? null : String.valueOf(body.get("remark"));
        transferService.approve(id, approved, remark);
        return Result.success();
    }
}
