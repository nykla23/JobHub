package com.cyd.xs.Response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Result<T> {
    private int code;       // 响应码（200=成功，500=失败）
    private String msg;     // 响应信息
    private T data;         // 响应数据
    private LocalDateTime timestamp; // 响应时间

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // 成功响应（带数据）
    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        result.setTimestamp(LocalDateTime.now());
        return result;
    }

    // 成功响应（无数据）
    public static <T> Result<T> success(String msg) {
        return success(msg, null);
    }

    // 失败响应
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        result.setTimestamp(LocalDateTime.now());
        return result;
    }
}