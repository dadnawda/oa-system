package org.example.web.service;

import org.example.web.pojo.entity.Dept;

import java.util.List;

public interface DeptService {

    List<Dept> listAll();

    void add(Dept dept);

    void update(Dept dept);

    void delete(Long id);
}
