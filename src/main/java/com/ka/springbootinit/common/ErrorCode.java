package com.ka.springbootinit.common;


public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "Incorrect request params"),
    NOT_LOGIN_ERROR(40100, "Not Login"),
    NO_AUTH_ERROR(40101, "No Auth"),
    NOT_FOUND_ERROR(40400, "Request Data Not Exist"),
    TOO_MANY_REQUEST_ERROR(40400, "Too Many Requests"),
    FORBIDDEN_ERROR(40300, "Request Forbidden"),
    SYSTEM_ERROR(50000, "Internal Error"),
    OPERATION_ERROR(50001, "Operation Failed");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
