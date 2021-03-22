package com.fenix.spirometer.room.model;

import androidx.annotation.IntDef;

import com.fenix.spirometer.model.BaseModel;

public class OperationResult<T extends BaseModel> {
    public T data;
    @ResultCode
    public int resultCode;
    public String operation;
    public String errMessage;

    public OperationResult(T data, String operation, int resultCode, String errMessage) {
        this.data = data;
        this.operation = operation;
        this.resultCode = resultCode;
        this.errMessage = errMessage;
    }

    public T getData() {
        return data;
    }

    @ResultCode
    public int getResultCode() {
        return resultCode;
    }

    public String getOperation() {
        return operation;
    }

    public static class Result {

    }

    public String getErrMessage() {
        return errMessage;
    }

    @IntDef({ResultCode.SUCCESS, ResultCode.FAIL})
    public @interface ResultCode {
        int SUCCESS = 0;
        int FAIL = 1;
    }
}
