package org.example.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web.common.BusinessException;
import org.example.web.common.PageBean;
import org.example.web.mapper.EmpMapper;
import org.example.web.mapper.SalaryMapper;
import org.example.web.pojo.dto.SalaryQueryParam;
import org.example.web.pojo.entity.Salary;
import org.example.web.service.SalaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryMapper salaryMapper;
    private final EmpMapper empMapper;

    @Override
    public PageBean<Salary> page(SalaryQueryParam param) {
        Long total = salaryMapper.count(param);
        List<Salary> rows = salaryMapper.page(param);
        return new PageBean<>(total, rows);
    }

    @Override
    public List<Salary> listMine() {
        return salaryMapper.listByEmpId(org.example.web.common.UserContext.currentEmpId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Salary salary) {
        validateMonth(salary.getSalaryMonth());
        if (salary.getEmpId() == null || empMapper.getById(salary.getEmpId()) == null) {
            throw new BusinessException("员工不存在");
        }
        // 职责分离：不能给自己录入工资
        if (salary.getEmpId().equals(org.example.web.common.UserContext.currentEmpId())) {
            throw new BusinessException("不能给自己录入工资");
        }
        if (salaryMapper.existsByEmpMonth(salary.getEmpId(), salary.getSalaryMonth()) > 0) {
            throw new BusinessException("该员工 " + salary.getSalaryMonth() + " 月工资记录已存在");
        }
        if (salary.getBaseSalary() == null) salary.setBaseSalary(0.0);
        if (salary.getBonus() == null) salary.setBonus(0.0);
        if (salary.getDeduction() == null) salary.setDeduction(0.0);
        // 实发工资由后端统一计算：基本 + 奖金 - 扣款
        salary.setActualPay(salary.getBaseSalary() + salary.getBonus() - salary.getDeduction());
        salary.setStatus(0);
        salaryMapper.insert(salary);
        log.info("录入工资：员工 {} {} 月，实发 {}", salary.getEmpId(), salary.getSalaryMonth(), salary.getActualPay());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Salary salary) {
        Salary db = getExisting(salary.getId());
        // 职责分离：不能调整自己的工资单
        if (db.getEmpId().equals(org.example.web.common.UserContext.currentEmpId())) {
            throw new BusinessException("不能给自己调整工资");
        }
        if (db.getStatus() == 1) {
            throw new BusinessException("该工资已发放，不允许调整");
        }
        if (salary.getBaseSalary() != null) db.setBaseSalary(salary.getBaseSalary());
        if (salary.getBonus() != null) db.setBonus(salary.getBonus());
        if (salary.getDeduction() != null) db.setDeduction(salary.getDeduction());
        db.setActualPay(db.getBaseSalary() + db.getBonus() - db.getDeduction());
        salaryMapper.update(db);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(Long id) {
        Salary db = getExisting(id);
        // 职责分离：不能给自己发放工资
        if (db.getEmpId().equals(org.example.web.common.UserContext.currentEmpId())) {
            throw new BusinessException("不能给自己发放工资");
        }
        // where 条件限定 status=0，影响行数为 0 说明已发放，保证幂等
        int rows = salaryMapper.markPaid(id, LocalDate.now());
        if (rows == 0) {
            throw new BusinessException("该工资已发放，请勿重复操作");
        }
        log.info("工资单 {} 发放成功", id);
    }

    private Salary getExisting(Long id) {
        if (id == null) {
            throw new BusinessException("工资单不存在");
        }
        Salary db = salaryMapper.getById(id);
        if (db == null) {
            throw new BusinessException("工资单不存在");
        }
        return db;
    }

    private void validateMonth(String month) {
        if (month == null || !month.matches("\\d{4}-\\d{2}")) {
            throw new BusinessException("工资月份格式应为 yyyy-MM");
        }
    }
}
