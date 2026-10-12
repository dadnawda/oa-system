package org.example.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web.common.BusinessException;
import org.example.web.common.PageBean;
import org.example.web.mapper.DeptMapper;
import org.example.web.mapper.EmpExprMapper;
import org.example.web.mapper.EmpMapper;
import org.example.web.pojo.dto.EmpQueryParam;
import org.example.web.pojo.dto.EmpRequest;
import org.example.web.pojo.entity.Emp;
import org.example.web.pojo.entity.EmpExpr;
import org.example.web.pojo.entity.User;
import org.example.web.service.EmpService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpServiceImpl implements EmpService {

    private final EmpMapper empMapper;
    private final EmpExprMapper empExprMapper;
    private final DeptMapper deptMapper;

    @Override
    public PageBean<Emp> page(EmpQueryParam param) {
        Long total = empMapper.count(param);
        List<Emp> rows = empMapper.page(param);
        return new PageBean<>(total, rows);
    }

    @Override
    public Emp detail(Long id) {
        Emp emp = empMapper.getById(id);
        if (emp == null) {
            throw new BusinessException("员工不存在");
        }
        emp.setExprList(empExprMapper.listByEmpId(id));
        return emp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(EmpRequest req) {
        validate(req);
        if (req.getDeptId() == null) {
            throw new BusinessException("新增员工必须选择所属部门（用于自动生成工号）");
        }
        if (deptMapper.countById(req.getDeptId()) == 0) {
            throw new BusinessException("所属部门不存在");
        }
        Emp emp = buildEmp(req);
        // 工号自动生成：部门号×100 + 该部门在职人数 + 1（如研发部[1]现有4人 -> 105）
        int activeCount = empMapper.countActiveByDept(req.getDeptId());
        int seq = activeCount + 1;
        if (seq > 99) {
            throw new BusinessException("该部门在职员工已满 99 人，无法生成工号");
        }
        long generatedId = req.getDeptId() * 100 + seq;
        // 撞号保护（历史工号占用时顺延）
        while (empMapper.existsById(generatedId) > 0 && seq < 99) {
            seq++;
            generatedId = req.getDeptId() * 100 + seq;
        }
        emp.setId(generatedId);
        empMapper.insert(emp);
        insertExprList(emp.getId(), req.getExprList());
        log.info("新增员工 {} -> 自动生成工号 {}，工作经历 {} 条", emp.getName(), emp.getId(),
                req.getExprList() == null ? 0 : req.getExprList().size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(EmpRequest req) {
        if (req.getId() == null || empMapper.getById(req.getId()) == null) {
            throw new BusinessException("员工不存在");
        }
        validate(req);
        if (req.getDeptId() != null && deptMapper.countById(req.getDeptId()) == 0) {
            throw new BusinessException("所属部门不存在");
        }
        Emp emp = buildEmp(req);
        emp.setId(req.getId());
        empMapper.update(emp);
        // 工作经历全删全插
        empExprMapper.deleteByEmpId(req.getId());
        insertExprList(req.getId(), req.getExprList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要操作的员工");
        }
        // 逻辑删除：标记离职留档，不物理删除
        empMapper.markResigned(ids);
        log.info("员工 {} 已标记离职（档案保留）", ids);
    }

    private void validate(EmpRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            throw new BusinessException("员工姓名不能为空");
        }
        if (req.getGender() == null || (req.getGender() != 1 && req.getGender() != 2)) {
            throw new BusinessException("性别取值不合法（1-男 / 2-女）");
        }
    }

    private Emp buildEmp(EmpRequest req) {
        Emp emp = new Emp();
        emp.setName(req.getName());
        emp.setGender(req.getGender());
        emp.setPhone(req.getPhone());
        emp.setJob(req.getJob());
        emp.setImage(req.getImage());
        emp.setDeptId(req.getDeptId());
        if (req.getEntryDate() != null && !req.getEntryDate().isBlank()) {
            try {
                emp.setEntryDate(LocalDate.parse(req.getEntryDate()));
            } catch (DateTimeParseException e) {
                throw new BusinessException("入职日期格式不合法，应为 yyyy-MM-dd");
            }
        }
        return emp;
    }

    private void insertExprList(Long empId, List<EmpExpr> exprList) {
        if (exprList == null || exprList.isEmpty()) {
            return;
        }
        for (EmpExpr expr : exprList) {
            expr.setEmpId(empId);
        }
        empExprMapper.batchInsert(exprList);
    }
}
