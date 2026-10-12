package org.example.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web.common.BusinessException;
import org.example.web.common.PageBean;
import org.example.web.common.UserContext;
import org.example.web.mapper.NoticeMapper;
import org.example.web.pojo.entity.Notice;
import org.example.web.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    public PageBean<Notice> page(Integer page, Integer pageSize, boolean isAdmin) {
        // 普通员工只能查看已发布公告
        Integer status = isAdmin ? null : 1;
        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 10 : pageSize;
        Long total = noticeMapper.count(status);
        List<Notice> rows = noticeMapper.page(status, (p - 1) * ps, ps);
        return new PageBean<>(total, rows);
    }

    @Override
    public Notice detail(Long id, boolean isAdmin) {
        Notice notice = noticeMapper.getById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        if (!isAdmin && notice.getStatus() != 1) {
            throw new BusinessException("公告不存在或未发布");
        }
        return notice;
    }

    @Override
    public void add(Notice notice) {
        if (notice.getTitle() == null || notice.getTitle().isBlank()) {
            throw new BusinessException("公告标题不能为空");
        }
        if (notice.getStatus() == null || (notice.getStatus() != 0 && notice.getStatus() != 1)) {
            notice.setStatus(0);
        }
        notice.setUserId(UserContext.currentEmpId());
        noticeMapper.insert(notice);
        log.info("发布公告 {} -> id={}，状态 {}", notice.getTitle(), notice.getId(),
                notice.getStatus() == 1 ? "已发布" : "草稿");
    }

    @Override
    public void update(Notice notice) {
        if (notice.getId() == null || noticeMapper.getById(notice.getId()) == null) {
            throw new BusinessException("公告不存在");
        }
        noticeMapper.update(notice);
    }

    @Override
    public void delete(Long id) {
        if (noticeMapper.getById(id) == null) {
            throw new BusinessException("公告不存在");
        }
        noticeMapper.deleteById(id);
    }
}
