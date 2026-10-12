package org.example.web.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.web.pojo.dto.StatusPageParam;
import org.example.web.pojo.entity.LeaveRequest;

import java.util.List;

/**
 * 请假审批 Mapper：审批列表为动态 SQL，见 resources/mapper/LeaveRequestMapper.xml
 */
public interface LeaveRequestMapper {

    List<LeaveRequest> page(StatusPageParam param);

    Long count(StatusPageParam param);

    List<LeaveRequest> listMine(Long empId);

    LeaveRequest getById(Long id);

    int insert(LeaveRequest leaveRequest);

    /**
     * 审批更新：仅 status=0 的单据会被更新（where 条件保证幂等），返回影响行数
     */
    @Update("update leave_request set status = #{status}, approver_id = #{approverId}, " +
            "approve_time = #{approveTime}, approve_remark = #{approveRemark} " +
            "where id = #{id} and status = 0")
    int approve(@Param("id") Long id, @Param("status") Integer status, @Param("approverId") Long approverId,
                @Param("approveTime") java.time.LocalDateTime approveTime, @Param("approveRemark") String approveRemark);
}
