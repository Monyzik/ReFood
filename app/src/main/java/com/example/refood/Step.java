package com.example.refood;

import android.widget.ImageView;

public class Step {
    private int time, number;
    private String info;
    private ImageView imageView;

    public Step(int number) {
        this.number = number;
    }

    public Step(int time, int number, String info, ImageView imageView) {
        this.time = time;
        this.number = number;
        this.info = info;
        this.imageView = imageView;
    }

    public int getTime() {
        return time;
    }

    public int getNumber() {
        return number;
    }
    public String getInfo() {
        return info;
    }
    public ImageView getImageView() {
        return imageView;
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
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

}
