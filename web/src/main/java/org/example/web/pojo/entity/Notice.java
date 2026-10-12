package org.example.web.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 公告表 notice（publisherName 为联查展示字段）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private LocalDateTime publishTime;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 联查字段：发布人姓名 */
    private String publisherName;
}
