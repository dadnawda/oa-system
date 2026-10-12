package org.example.web.service;

import org.example.web.common.PageBean;
import org.example.web.pojo.dto.StatusPageParam;
import org.example.web.pojo.entity.JobTransfer;

import java.util.List;

public interface TransferService {

    List<JobTransfer> listMine();

    void submit(JobTransfer jobTransfer);

    PageBean<JobTransfer> page(StatusPageParam param);

    void approve(Long id, boolean approved, String remark);
}
