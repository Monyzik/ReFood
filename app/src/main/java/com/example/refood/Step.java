package com.example.refood;

import android.widget.ImageView;

public class Step {
    private int time, number;
    private String info;
    private String imagePath;

    public Step(int number) {
        this.number = number;
    }

    public Step(int time, int number, String info, String imagePath) {
        this.time = time;
        this.number = number;
        this.info = info;
        this.imagePath = imagePath;
    }

    public Step() {}

    public int getTime() {
        return time;
    }

    public int getNumber() {
        return number;
    }
    public String getInfo() {
        return info;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setTime(int time) {
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
