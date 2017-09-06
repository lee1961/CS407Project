package com.example.ezclassapp.Models;

/**
 * Created by tychan on 9/2/2017.
 */

public class Comment {
    private String userUID;
    private String comment;

    // Default constructor needed by Firebase
    public Comment() {
    }

    public Comment(String userUID, String comment) {
        this.userUID = userUID;
        this.comment = comment;
    }

    public String getName() {
        return userUID;
    }

    public void setName(String userUID) {
        this.userUID = userUID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "userUID='" + userUID + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
