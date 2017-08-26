package com.example.ezclassapp.Models;

import java.util.List;
import java.util.UUID;

/**
 * Created by victorlee95 on 8/26/2017.
 */

public class Class {
    String ID;
    String className;
    String imageUrl;
    List<UUID> reviewID_list;

    public Class(String ID, String className) {
        this.ID = ID;
        this.className = className;
    }

    public String getID() {
        return ID;
    }

    public void setId(String ID) {
        this.ID = ID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<UUID> getReviewID_list() {
        return reviewID_list;
    }

    public void setReviewID_list(List<UUID> reviewID_list) {
        this.reviewID_list = reviewID_list;
    }
}
