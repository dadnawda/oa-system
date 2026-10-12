package org.example.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web.common.BusinessException;
import org.example.web.common.PageBean;
import org.example.web.common.UserContext;
import org.example.web.mapper.DeptMapper;
import org.example.web.mapper.EmpMapper;
import org.example.web.mapper.JobTransferMapper;
import org.example.web.pojo.dto.StatusPageParam;
import org.example.web.pojo.entity.JobTransfer;
import org.example.web.service.TransferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final JobTransferMapper jobTransferMapper;
    private final EmpMapper empMapper;
    private final DeptMapper deptMapper;

    @Override
    public List<JobTransfer> listMine() {
        return jobTransferMapper.listMine(UserContext.currentEmpId());
    }

    @Override
    public void submit(JobTransfer req) {
        if (req.getTargetJob() == null || req.getTargetJob().isBlank()) {
            throw new BusinessException("目标职位不能为空");
        }
        Long empId = UserContext.currentEmpId();
        if (req.getTargetDeptId() != null && deptMapper.countById(req.getTargetDeptId()) == 0) {
            throw new BusinessException("目标部门不存在");
        }
        if (empMapper.getById(empId) == null) {
            throw new BusinessException("员工信息不存在");
        }
        // 快照当前职位，防止后续 emp.job 变动导致申请记录失真
        req.setEmpId(empId);
        req.setCurrentJob(empMapper.getById(empId).getJob());
        req.setStatus(0);
        jobTransferMapper.insert(req);
        log.info("员工 {} 提交调岗申请 {} -> {} -> id={}", empId, req.getCurrentJob(), req.getTargetJob(), req.getId());
    }

    @Override
    public PageBean<JobTransfer> page(StatusPageParam param) {
        Long total = jobTransferMapper.count(param);
        List<JobTransfer> rows = jobTransferMapper.page(param);
        return new PageBean<>(total, rows);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, boolean approved, String remark) {
        JobTransfer db = getExisting(id);
        if (db.getStatus() != 0) {
            throw new BusinessException("该调岗申请已审批，请勿重复操作");
        }
        // 职责分离：管理员不能审批自己提交的调岗申请
        if (db.getEmpId().equals(UserContext.currentEmpId())) {
            throw new BusinessException("不能审批自己提交的职位调整申请");
        }
        // where 条件限定 status=0，影响行数为 0 说明并发下已被审批，保证幂等
        int rows = jobTransferMapper.approve(id, approved ? 1 : 2,
                UserContext.currentEmpId(), LocalDateTime.now(), remark);
        if (rows == 0) {
            throw new BusinessException("该调岗申请已审批，请勿重复操作");
        }
        // 审批通过：同一事务内将申请值落回员工表
        if (approved) {
            if (db.getTargetDeptId() != null) {
                empMapper.updateJobAndDept(db.getEmpId(), db.getTargetJob(), db.getTargetDeptId());
            } else {
                empMapper.updateJob(db.getEmpId(), db.getTargetJob());
            }
            log.info("调岗申请 {} 审批通过，员工 {} 职位已同步为 {}", id, db.getEmpId(), db.getTargetJob());
        }
    }

    private JobTransfer getExisting(Long id) {
        if (id == null) {
            throw new BusinessException("调岗申请不存在");
        }
        JobTransfer db = jobTransferMapper.getById(id);
        if (db == null) {
            throw new BusinessException("调岗申请不存在");
        }
        return db;
    }
}
