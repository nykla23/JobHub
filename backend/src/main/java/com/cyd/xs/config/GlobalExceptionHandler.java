package com.cyd.xs.config;

import com.cyd.xs.Utils.ResultUtil;
import com.cyd.xs.Utils.ResultVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResultVO<Void>> handleRuntimeException(RuntimeException e) {
        // ⭐⭐ 关键：用日志，而不是 printStackTrace
        System.err.println("====== RuntimeException ======");
        e.printStackTrace();

        // 调试期：把异常类型 + message 都返回
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultUtil.error(
                        400,
                        e.getClass().getSimpleName() + ": " + e.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultVO<Void>> handleException(Exception e) {

        System.err.println("====== Exception ======");
        e.printStackTrace();

        // 🔥 关键：拿到真正的底层异常
        Throwable cause = e.getCause();

        if (cause != null) {
            System.err.println("====== Root Cause ======");
            cause.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultUtil.error(
                            500,
                            cause.getClass().getName() + ": " + cause.getMessage()
                    ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultUtil.error(
                        500,
                        e.getClass().getName() + ": " + e.getMessage()
                ));
    }

}

