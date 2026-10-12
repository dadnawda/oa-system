package org.example.web.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.web.pojo.dto.StatusPageParam;
import org.example.web.pojo.entity.JobTransfer;

import java.util.List;

/**
 * 职位调整 Mapper：审批列表为动态 SQL，见 resources/mapper/JobTransferMapper.xml
 */
public interface JobTransferMapper {

    List<JobTransfer> page(StatusPageParam param);

    Long count(StatusPageParam param);

    List<JobTransfer> listMine(Long empId);

    JobTransfer getById(Long id);

    int insert(JobTransfer jobTransfer);

    /**
     * 审批更新：仅 status=0 的单据会被更新（where 条件保证幂等），返回影响行数
     */
    @Update("update job_transfer set status = #{status}, approver_id = #{approverId}, " +
            "approve_time = #{approveTime}, approve_remark = #{approveRemark} " +
            "where id = #{id} and status = 0")
    int approve(@Param("id") Long id, @Param("status") Integer status, @Param("approverId") Long approverId,
                @Param("approveTime") java.time.LocalDateTime approveTime, @Param("approveRemark") String approveRemark);
}
