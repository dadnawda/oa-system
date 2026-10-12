package org.example.web.pojo.dto;

import lombok.Data;

/**
 * 工资分页查询参数
 */
@Data
public class SalaryQueryParam {

    /** null 表示全部员工 */
    private Long empId;
    /** null 表示全部月份 */
    private String salaryMonth;
    private Integer page = 1;
    private Integer pageSize = 10;

    public Integer getOffset() {
        return (page - 1) * pageSize;
    }
}
