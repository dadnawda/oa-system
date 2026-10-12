package org.example.web.common;

import lombok.Getter;

/**
 * 业务异常：由 GlobalExceptionHandler 统一转换为 Result.error
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 1;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
