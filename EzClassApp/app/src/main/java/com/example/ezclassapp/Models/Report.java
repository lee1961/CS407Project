package com.example.ezclassapp.Models;

import java.util.Map;
import java.util.HashMap;
/**
 * Created by zuoyangding on 11/14/17.
 */

public class Report {
    private String reviewID;
    private String review;
    private String userID;
    private String reviewUID;
    private String classID;

    public Report(String userID){
        this.userID = userID;
    }

    public String getUserID() {return this.userID;}
    public void setUserID(String userID) {this.userID = userID;}

    public String getReview() {return this.review;}
    public void setReview(String review) {this.review = review;}

    public void setReviewUID (String reviewUID) {this.reviewUID = reviewUID;}
    public String getReviewUID() {return this.reviewUID;}

    public void setReviewID(String reviewID) {this.reviewID = reviewID;}
    public String getReviewID() {return this.reviewID;}

    public void setClassID(String classID) {this.classID = classID;}
    public String getClassID() {return this.classID;}
}
