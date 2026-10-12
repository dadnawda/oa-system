package org.example.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web.common.BusinessException;
import org.example.web.common.PageBean;
import org.example.web.common.UserContext;
import org.example.web.mapper.EmpMapper;
import org.example.web.mapper.LeaveRequestMapper;
import org.example.web.pojo.dto.StatusPageParam;
import org.example.web.pojo.entity.LeaveRequest;
import org.example.web.service.LeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestMapper leaveRequestMapper;
    private final EmpMapper empMapper;

    @Override
    public List<LeaveRequest> listMine() {
        return leaveRequestMapper.listMine(UserContext.currentEmpId());
    }

    @Override
    public void submit(LeaveRequest req) {
        if (req.getLeaveType() == null || req.getLeaveType() < 1 || req.getLeaveType() > 5) {
            throw new BusinessException("请假类型不合法（1-5）");
        }
        if (req.getBeginDate() == null || req.getEndDate() == null) {
            throw new BusinessException("请假起止日期不能为空");
        }
        if (req.getEndDate().isBefore(req.getBeginDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }
        if (req.getDays() == null || req.getDays() <= 0) {
            throw new BusinessException("请假天数必须大于 0");
        }
        req.setEmpId(UserContext.currentEmpId());
        req.setStatus(0);
        leaveRequestMapper.insert(req);
        log.info("员工 {} 提交请假申请 -> id={}", req.getEmpId(), req.getId());
    }

    @Override
    public PageBean<LeaveRequest> page(StatusPageParam param) {
        Long total = leaveRequestMapper.count(param);
        List<LeaveRequest> rows = leaveRequestMapper.page(param);
        return new PageBean<>(total, rows);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, boolean approved, String remark) {
        LeaveRequest db = getExisting(id);
        if (db.getStatus() != 0) {
            throw new BusinessException("该请假单已审批，请勿重复操作");
        }
        // 职责分离：管理员不能审批自己提交的请假单
        if (db.getEmpId().equals(UserContext.currentEmpId())) {
            throw new BusinessException("不能审批自己提交的请假申请");
        }
        // where 条件限定 status=0，影响行数为 0 说明并发下已被审批，保证幂等
        int rows = leaveRequestMapper.approve(id, approved ? 1 : 2,
                UserContext.currentEmpId(), LocalDateTime.now(), remark);
        if (rows == 0) {
            throw new BusinessException("该请假单已审批，请勿重复操作");
        }
        log.info("请假单 {} 审批{}，审批人 {}", id, approved ? "通过" : "驳回", UserContext.currentEmpId());
    }

    private LeaveRequest getExisting(Long id) {
        if (id == null) {
            throw new BusinessException("请假单不存在");
        }
        LeaveRequest db = leaveRequestMapper.getById(id);
        if (db == null) {
            throw new BusinessException("请假单不存在");
        }
        return db;
    }
}
