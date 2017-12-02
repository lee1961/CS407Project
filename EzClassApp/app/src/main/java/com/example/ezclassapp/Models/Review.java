package com.example.ezclassapp.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by victorlee95 on 8/26/2017.
 */

public class Review {
    private String ID;
    private String reviewerName;
    private String foreignID_classID;
    private String foreignID_userID;
    private String opinion;
    private String tips;
    private int difficulty;
    private int usefulness;
    private int upvote;
    private int downvote;
    private String review;
    private List<String> commentUID;
    private Map<String, Boolean> checkUserVoted;
    private Map<String, Heart> userHeart;
    private boolean postAnon;
    public Review() {
        this.commentUID = new ArrayList<>();
        this.checkUserVoted = new HashMap<>();
        this.userHeart = new HashMap<>();
    }

    public Review(String ID, String reviewerName, String foreignID_classID, String opinion, String foreignID_userID,
                  String review, int difficulty, int usefulness) {
        this.ID = ID;
        this.reviewerName = reviewerName;
        this.foreignID_classID = foreignID_classID;
        this.opinion = opinion;
        this.foreignID_userID = foreignID_userID;
        checkUserVoted = new HashMap<>();
        checkUserVoted.put(ID, true);
        this.tips = review;
        this.difficulty = difficulty;
        this.usefulness = usefulness;
        this.commentUID = new ArrayList<>();
        this.checkUserVoted = new HashMap<>();
        this.userHeart = new HashMap<>();
    }

    public Review(String ID, String reviewerName, String foreignID_classID, String foreignID_userID,
                  String opinion, String tips, int difficulty, int usefulness, boolean postAnon) {
        this.ID = ID;
        this.reviewerName = reviewerName;
        this.foreignID_classID = foreignID_classID;
        this.foreignID_userID = foreignID_userID;
        this.opinion = opinion;
        this.tips = tips;
        this.difficulty = difficulty;
        this.usefulness = usefulness;
        this.postAnon = postAnon;
        this.commentUID = new ArrayList<>();
        this.checkUserVoted = new HashMap<>();
        this.userHeart = new HashMap<>();
    }


    public Map<String, Heart> getUserHeart() {
        return userHeart;
    }

    public void setUserHeart(Map<String, Heart> userHeart) {
        this.userHeart = userHeart;
    }

    public boolean isPostAnon() {
        return postAnon;
    }

    public void setPostAnon(boolean postAnon) {
        this.postAnon = postAnon;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
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

    public Map<String, Boolean> getCheckUserVoted() {
        return checkUserVoted;
    }

    public void setCheckUserVoted(Map<String, Boolean> checkUserVoted) {
        this.checkUserVoted = checkUserVoted;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Override
    public String toString() {
        return "Review{" +
                "ID='" + ID + '\'' +
                ", reviewerName='" + reviewerName + '\'' +
                ", foreignID_classID='" + foreignID_classID + '\'' +
                ", foreignID_userID='" + foreignID_userID + '\'' +
                ", opinion='" + opinion + '\'' +
                ", tips='" + tips + '\'' +
                ", difficulty=" + difficulty +
                ", usefulness=" + usefulness +
                ", upvote=" + upvote +
                ", downvote=" + downvote +
                ", review='" + review + '\'' +
                ", commentUID=" + commentUID +
                ", checkUserVoted=" + checkUserVoted +
                ", userHeart=" + userHeart +
                ", postAnon=" + postAnon +
                '}';
    }
}
