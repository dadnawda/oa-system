package org.example.web.service;

import org.example.web.common.PageBean;
import org.example.web.pojo.dto.SalaryQueryParam;
import org.example.web.pojo.entity.Salary;

import java.util.List;

public interface SalaryService {

    PageBean<Salary> page(SalaryQueryParam param);

    List<Salary> listMine();

    void add(Salary salary);

    void update(Salary salary);

    void pay(Long id);
}
