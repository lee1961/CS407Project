package com.example.ezclassapp.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by victorlee95 on 8/26/2017.
 */

public class Class {
    String id;
    String courseName;
    String courseNumber;
    String imageUrl;
    List<UUID> reviewID_list;

    //    public Class(String ID, String courseName) {
//        this.ID = ID;
//        this.courseName = courseName;
//    }
    public Class() {

    }

    public Class(String courseName) {
        this.courseName = courseName;
    }

    public Class(String courseNumber, String courseName) {
        this.courseName = courseName;
        this.courseNumber = courseNumber;
    }

    public static List<Class> getDummyClassList() {
        List<Class> classList = new ArrayList<Class>();
        classList.add(new Class("CS354", "Operating System"));
        classList.add(new Class("CS448", "Database Systems"));
        classList.add(new Class("CS240", "C Programming"));
        classList.add(new Class("CS180", "Java Programming"));
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
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
