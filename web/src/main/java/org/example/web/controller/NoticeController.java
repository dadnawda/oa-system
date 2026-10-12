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
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestHeader(value = "X-View-Identity", required = false) String viewIdentity) {
        return Result.success(noticeService.page(page, pageSize, canSeeDraft(viewIdentity)));
    }

    @GetMapping("/api/notices/{id}")
    public Result<Notice> detail(@PathVariable Long id,
                                 @RequestHeader(value = "X-View-Identity", required = false) String viewIdentity) {
        return Result.success(noticeService.detail(id, canSeeDraft(viewIdentity)));
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

    /**
     * 能否看到草稿：
     * 需要真实角色是管理员，且当前不是以"员工身份"进入（前端通过 X-View-Identity 头传递登录身份）。
     * 这样"管理员以员工身份进入"时看到的就是员工视角 —— 草稿不显示，但权限本身不受影响。
     */
    private boolean canSeeDraft(String viewIdentity) {
        return isAdmin() && !"staff".equalsIgnoreCase(viewIdentity);
    }
}
