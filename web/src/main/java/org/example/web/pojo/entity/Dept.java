package org.example.web.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 部门表 dept
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dept {

    private Long id;
    private String name;
    private String description;
    /** 是否已删除: 0-正常 / 1-已删除(留档不显示) */
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
