package org.example.web.service;

import org.example.web.common.PageBean;
import org.example.web.pojo.dto.EmpQueryParam;
import org.example.web.pojo.dto.EmpRequest;
import org.example.web.pojo.entity.Emp;

import java.util.List;

public interface EmpService {

    PageBean<Emp> page(EmpQueryParam param);

    Emp detail(Long id);

    void add(EmpRequest req);

    void update(EmpRequest req);

    void deleteBatch(List<Long> ids);
}
