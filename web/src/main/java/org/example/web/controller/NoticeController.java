package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.common.LoginUser;
import org.example.web.common.Result;
import org.example.web.common.UserContext;
import org.example.web.pojo.entity.Notice;
import org.example.web.service.NoticeService;
import org.springframework.web.bind.annotation.*;

/**
 * 公告模块：查看（登录即可）+ 管理（管理员专用）
 */
@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/api/notices")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(noticeService.page(page, pageSize, isAdmin()));
    }

    @GetMapping("/api/notices/{id}")
    public Result<Notice> detail(@PathVariable Long id) {
        return Result.success(noticeService.detail(id, isAdmin()));
    }

    @PostMapping("/api/admin/notices")
    public Result<Void> add(@RequestBody Notice notice) {
        noticeService.add(notice);
        return Result.success();
    }

    @PutMapping("/api/admin/notices/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Notice notice) {
        notice.setId(id);
        noticeService.update(notice);
        return Result.success();
    }

    @DeleteMapping("/api/admin/notices/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success();
    }

    private boolean isAdmin() {
        LoginUser user = UserContext.get();
        return user != null && "admin".equals(user.getRole());
    }
}
