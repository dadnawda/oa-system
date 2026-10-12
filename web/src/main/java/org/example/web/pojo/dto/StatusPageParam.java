package org.example.web.pojo.dto;

import lombok.Data;

/**
 * 通用"状态 + 分页"查询参数（请假审批 / 调岗审批列表复用）
 */
@Data
public class StatusPageParam {

    /** null 表示查全部状态 */
    private Integer status;
    private Integer page = 1;
    private Integer pageSize = 10;

    public Integer getOffset() {
        return (page - 1) * pageSize;
    }
}
