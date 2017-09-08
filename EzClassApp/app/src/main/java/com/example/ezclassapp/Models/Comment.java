package com.example.ezclassapp.Models;

/**
 * Created by tychan on 9/2/2017.
 */

public class Comment {
    private String userUID;
    private String comment;
    private String commentUID;

    // Default constructor needed by Firebase
    public Comment() {
    }

    public Comment(String userUID, String comment, String commentUID) {
        this.userUID = userUID;
        this.comment = comment;
        this.commentUID = commentUID;
    }

    public String getCommentUID() {
        return commentUID;
    }

    public void setCommentUID(String commentUID) {
        this.commentUID = commentUID;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
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
                ", commentUID='" + commentUID + '\'' +
                '}';
    }

}
