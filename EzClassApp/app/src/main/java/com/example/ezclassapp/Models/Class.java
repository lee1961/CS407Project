package com.example.ezclassapp.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by victorlee95 on 8/26/2017.
 */

public class Class {
    String ID;
    String courseName;
    String courseNumber;
    String imageUrl;
    List<UUID> reviewID_list;

//    public Class(String ID, String courseName) {
//        this.ID = ID;
//        this.courseName = courseName;
//    }
    public Class(String courseName) {
        this.courseName = courseName;
    }

    public Class(String courseNumber, String courseName) {
        this.courseName = courseName;
        this.courseName = courseNumber;
    }

    public String getID() {
        return ID;
    }

    public void setId(String ID) {
        this.ID = ID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setClassName(String courseName) {
        this.courseName = courseName;
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

    public static List<Class> getDummyClassList() {
        List<Class> classList = new ArrayList<Class>();
        classList.add(new Class("CS354","Operating System"));
        classList.add(new Class("CS448","Operating System"));
        classList.add(new Class("CS333","Operating System"));
        classList.add(new Class("CS354","Operating System"));
        return classList;
//        butto.setonclicSiter {
//
//            List<Class> classes = Class.getDummyClassList();
//            for(Class class: classes) {
//                String key = database.getReference("Class").push().getKey();
//                String id = generateID;
//                class.setID(id);
//
//            }
//        }
    }
}
