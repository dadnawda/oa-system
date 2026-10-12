package org.example.web.service;

import org.example.web.common.PageBean;
import org.example.web.pojo.entity.Notice;

public interface NoticeService {

    PageBean<Notice> page(Integer page, Integer pageSize, boolean isAdmin);

    Notice detail(Long id, boolean isAdmin);

    void add(Notice notice);

    void update(Notice notice);

    void delete(Long id);
}
