package com.example.ezclassapp.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by victorlee95 on 8/26/2017.
 */

public class Review {
    String ID;
    String reviewerName;
    String foreignID_classID;
    String foreignID_userID;
    String opinion;
    String tips;
    int difficulty;
    int usefulness;
    int upvote;
    int downvote;
    HashMap<String,Boolean> checkUserVoted;

    public Review() {

    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public Review(String ID,String reviewerName, String foreignID_classID, String opinion) {
        this.ID = ID;
        this.reviewerName = reviewerName;
        this.foreignID_classID = foreignID_classID;
        this.opinion = opinion;
    }

    public String getId() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getForeignID_classID() {
        return foreignID_classID;
    }

    public void setForeignID_classID(String foreignID_classID) {
        this.foreignID_classID = foreignID_classID;
    }

    public String getForeignID_userID() {
        return foreignID_userID;
    }

    public void setForeignID_userID(String foreignID_userID) {
        this.foreignID_userID = foreignID_userID;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getUsefulness() {
        return usefulness;
    }

    public void setUsefulness(int usefulness) {
        this.usefulness = usefulness;
    }

    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvote) {
        this.upvote = upvote;
    }

    public int getDownvote() {
        return downvote;
    }

    public void setDownvote(int downvote) {
        this.downvote = downvote;
    }

    public HashMap<String, Boolean> getCheckUserVoted() {
        return checkUserVoted;
    }

    public void setCheckUserVoted(HashMap<String, Boolean> checkUserVoted) {
        this.checkUserVoted = checkUserVoted;
    }
}
