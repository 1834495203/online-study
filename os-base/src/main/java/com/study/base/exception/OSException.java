package com.study.base.exception;

public class OSException extends RuntimeException {

    private String errorMsg;

    public OSException() {
        super();
    }

    public OSException(String message) {
        super(message);
        this.errorMsg = message;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public static void cast(String msg){
        throw new OSException(msg);
    }

    public static void cast(CommonError error){
        throw new OSException(error.getErrMessage());
    }
}
