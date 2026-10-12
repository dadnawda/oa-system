package org.example.web.service;

import org.example.web.common.PageBean;
import org.example.web.pojo.dto.EmpQueryParam;
import org.example.web.pojo.dto.EmpRequest;
import org.example.web.pojo.entity.Emp;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmpService {

    PageBean<Emp> page(EmpQueryParam param);

    Emp detail(Long id);

    void add(EmpRequest req);

    void update(EmpRequest req);

    void deleteBatch(List<Long> ids);

    /** 上传员工个人照片（管理员操作），返回可访问的图片 URL */
    String uploadPhoto(Long empId, MultipartFile file);
}
