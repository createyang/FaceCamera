package com.quansoon.facecamera.model;


public class BaseResultBean<T> {
    /**
     * retCode : 000000
     * message : 成功
     * result : {"id":1582,"name":"方建辉","groupName":"全容公司","job":"JAVA工程师","verifyFace":"","installType":"","installTypeStr":"","ioTimeStr":""}
     */
    private String retCode;
    private String message;
    private T result;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public static class ResultBean {

    }

//    private String retCode;
//    private String message;
//    private ArrayList<T> result;
//
//
//    public ArrayList<T> getResult() {
//        return result;
//    }
//
//    public void setResult(ArrayList<T> result) {
//        this.result = result;
//    }
//
//
//    public String getRetCode() {
//        return retCode;
//    }
//
//    public void setRetCode(String retcode) {
//        this.retCode = retcode;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//


}
