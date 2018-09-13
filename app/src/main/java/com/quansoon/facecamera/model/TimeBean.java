package com.quansoon.facecamera.model;

/**
 * Created by Administrator on 2018/6/21.
 */

public class TimeBean {
    /**
     * {
     * "time": "14:39:17",
     * "date": "09月09日"
     * "dateFm": "星期日",
     * "lunar": "七月卅十",
     * }
     */
    private String time;
    private String date;
    private String dateFm;
    private String lunar;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateFm() {
        return dateFm;
    }

    public void setDateFm(String dateFm) {
        this.dateFm = dateFm;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLunar() {
        return lunar;
    }

    public void setLunar(String lunar) {
        this.lunar = lunar;
    }
}
