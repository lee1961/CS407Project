package com.example.ezclassapp.Models;

/**
 * Created by victorlee95 on 8/24/2017.
 */

public class User {
    private String name;
    private String major;
    private String image;
    private String thumb_image;
    private int karmaPoints;
    private int age;

    public User() {

    }

    public User(String name, int age) {

        this.name = name;
    }

    public User(String name, String major, String image, String thumb_image) {
        this.name = name;
        this.major = major;
        this.image = image;
        this.thumb_image = thumb_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", major='" + major + '\'' +
                ", image='" + image + '\'' +
                ", thumb_image='" + thumb_image + '\'' +
                ", karmaPoints=" + karmaPoints +
                ", age=" + age +
                '}';
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

}
