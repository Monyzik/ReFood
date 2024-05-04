package com.example.refood;

import com.google.gson.annotations.Expose;

public class Step {
    @Expose
    private long number;
    @Expose
    private String info, time;
    @Expose
    private String imagePath;


    public Step(int number) {
        this.number = number;
    }

    public Step(String time, int number, String info, String imagePath) {
        this.time = time;
        this.number = number;
        this.info = info;
        this.imagePath = imagePath;
    }

    public Step() {}

    public String getTime() {
        return time;
    }

    public long getNumber() {
        return number;
    }
    public String getInfo() {
        return info;
    }
    public String getImagePath() {
        return imagePath;
    }


    public void setTime(String time) {
        this.time = time;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


}
