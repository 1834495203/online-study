package com.study.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * 通用全局异常
 */
@ControllerAdvice // 控制器增强
@Slf4j
public class GlobalExceptionHandler {

    //捕获OSException可知异常
    @ExceptionHandler(OSException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //服务器内部错误
    @ResponseBody
    public RestErrorResponse doOSException(OSException e){
        String errorMsg = e.getErrorMsg();

        log.error("异常信息捕获: "+errorMsg);
        e.printStackTrace();

        return new RestErrorResponse(errorMsg);
    }

    //捕获不可知异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public RestErrorResponse doException(Exception e){
        String errorMsg = e.getMessage();

        log.error("异常信息捕获: "+errorMsg);
        e.printStackTrace();

        return new RestErrorResponse(errorMsg);
    }

    //捕获变量校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public RestErrorResponse doMethodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        //校验的错误信息
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        //收集错误
        StringBuffer errors = new StringBuffer();
        fieldErrors.forEach(error-> errors.append(error.getDefaultMessage()).append(", "));

        return new RestErrorResponse(errors.toString());
    }
}
