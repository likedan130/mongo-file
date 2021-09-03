package com.loctek.file.util;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @function http响应的基类
 * @param <T>
 * @author wneck130@gmail.com
 */
@Data
public class ResultMsg<T> implements Serializable {

    private static Integer OK = 200;
    private static Integer INTERNAL_SERVER_ERROR = 500;
    /**
     * HTTPSTATUS,默认为成功
     */
    Integer resultCode = OK;
    /**
     * 响应成功时的数据
     */
    T data;
    /**
     * 错误信息
     */
    String message;
    /**
     * 时间戳
     */
    Date timestamp;

    public ResultMsg(T data) {
        this.data = data;
    }

    public ResultMsg() {
        this.message = "操作成功";
    }

    public static <T> ResultMsg<T> error(String message) {
        return new ResultMsg<T>()
                .putMessage(message)
                .putCode(INTERNAL_SERVER_ERROR)
                .putTimestamp(new Date());
    }
    
    public static <T> ResultMsg<T> error(Integer resultCode, String message) {
    	ResultMsg<T> resultMsg = new ResultMsg<T>()
        .putMessage(message)
        .putTimestamp(new Date());
    	
    	resultMsg.setResultCode(resultCode);
        return resultMsg;
    }

    public static <T> ResultMsg<T> ok(T data) {
        return new ResultMsg(data)
                .putCode(OK)
                .putMessage("操作成功")
                .putTimestamp(new Date());
    }

    public static <T> ResultMsg<T> ok() {
        return ok(null);
    }

    /**
     * 返回服务器异常信息
     * @param resultCode
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ResultMsg<T> exception(Integer resultCode, String message) {
        return new ResultMsg()
                .putCode(resultCode)
                .putMessage(message)
                .putTimestamp(new Date());
    }

    ResultMsg<T> putMessage(String message) {
        this.message = message;
        return this;
    }

    ResultMsg<T> putCode(Integer httpStatus) {
        this.resultCode = httpStatus;
        return this;
    }

    ResultMsg<T> putTimestamp(Date date) {
        this.timestamp = date;
        return this;
    }

}
