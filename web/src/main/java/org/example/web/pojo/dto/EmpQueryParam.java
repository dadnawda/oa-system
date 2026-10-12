package org.example.web.pojo.dto;

import lombok.Data;

/**
 * 员工分页查询参数
 */
@Data
public class EmpQueryParam {

    private String name;
    private Integer gender;
    private String begin;
    private String end;
    /** 在职状态筛选: 1-在职(默认) / 0-离职 / null-全部 */
    private Integer status = 1;
    private Integer page = 1;
    private Integer pageSize = 10;

    public Integer getOffset() {
        return (page - 1) * pageSize;
    }
}
