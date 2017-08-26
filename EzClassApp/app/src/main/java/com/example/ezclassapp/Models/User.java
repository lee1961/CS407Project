package com.example.ezclassapp.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victorlee95 on 8/24/2017.
 */

public class User {
    String name;
    String major;
    String image;
    String thumb_image;

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public String getImage() {
        return image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public User(String name, String major, String image, String thumb_image) {
        this.name = name;
        this.major = major;
        this.image = image;
        this.thumb_image = thumb_image;
    }

}
