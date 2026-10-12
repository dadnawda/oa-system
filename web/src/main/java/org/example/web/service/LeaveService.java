package org.example.web.service;

import org.example.web.common.PageBean;
import org.example.web.pojo.dto.StatusPageParam;
import org.example.web.pojo.entity.LeaveRequest;

import java.util.List;

public interface LeaveService {

    List<LeaveRequest> listMine();

    void submit(LeaveRequest leaveRequest);

    PageBean<LeaveRequest> page(StatusPageParam param);

    void approve(Long id, boolean approved, String remark);
}
