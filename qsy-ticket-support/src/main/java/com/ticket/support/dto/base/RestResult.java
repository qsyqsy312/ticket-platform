package com.ticket.support.dto.base;

public final class RestResult<T> {

    private T data;

    private String msg;

    private boolean success;

    private RestResult(T data, String msg, boolean success) {
        this.data = data;
        this.msg = msg;
        this.success = success;
    }

    public static <T> RestResult<T> success(T data) {
        return new RestResult<>(data, null, Boolean.TRUE);
    }

    public static <T> RestResult<T> success(T data,String msg) {
        return new RestResult<>(data, msg, Boolean.TRUE);
    }

    public static <T> RestResult<T> fail(String msg) {
        return new RestResult<>(null, msg, Boolean.FALSE);
    }

    public static <T> RestResult<T> fail(Throwable t) {
        return new RestResult<>(null, t.getMessage(), Boolean.FALSE);
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
