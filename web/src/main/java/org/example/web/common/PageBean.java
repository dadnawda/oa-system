package org.example.web.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 手写分页结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageBean<T> {

    private Long total;
    private List<T> rows;
}
