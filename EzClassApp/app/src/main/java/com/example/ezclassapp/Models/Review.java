package com.example.ezclassapp.Models;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by victorlee95 on 8/26/2017.
 */

public class Review {
    private String ID;
    private String foreignID_classID;
    private UUID foreignID_userID;
    private String opinion;
    private String tips;
    private int difficulty;
    private int usefulness;
    private int upvote;
    private int downvote;
    private HashMap<String, Boolean> checkUserVoted;
    private List<String> commentUID;

    public Review() {

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<String> getCommentUID() {
        return commentUID;
    }

    public void setCommentUID(List<String> commentUID) {
        this.commentUID = commentUID;
    }

    public String getForeignID_classID() {
        return foreignID_classID;
    }

    public void setForeignID_classID(String foreignID_classID) {
        this.foreignID_classID = foreignID_classID;
    }

    public UUID getForeignID_userID() {
        return foreignID_userID;
    }

    public void setForeignID_userID(UUID foreignID_userID) {
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
