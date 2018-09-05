package com.example.administrator.attendanceinputv3.model;


import java.util.ArrayList;
import java.util.List;

public class BaseResultBean<T> {

    private String retCode;
    private String message;
    private ArrayList<T> result;


    public ArrayList<T> getResult() {
        return result;
    }

    public void setResult(ArrayList<T> result) {
        this.result = result;
    }


    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retcode) {
        this.retCode = retcode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
