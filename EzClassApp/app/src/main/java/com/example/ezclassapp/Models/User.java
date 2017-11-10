package com.example.ezclassapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by victorlee95 on 8/24/2017.
 */

public class User implements Parcelable {
    private String name;
    private String major;
    private String image;
    private String thumb_image;
    private int karmaPoints;
    private int postCount;
    private boolean isAdmin;

    public User() {

    }

    public User(String name) {
        this.name = name;
    }

    public User(String name, String major, String image, String thumb_image) {
        this.name = name;
        this.major = major;
        this.image = image;
        this.thumb_image = thumb_image;
    }

    public User(String name, String major, String image, String thumb_image, int karmaPoints, int postCount, boolean isAdmin) {
        this.name = name;
        this.major = major;
        this.image = image;
        this.thumb_image = thumb_image;
        this.karmaPoints = karmaPoints;
        this.postCount = postCount;
        this.isAdmin = isAdmin;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", major='" + major + '\'' +
                ", image='" + image + '\'' +
                ", thumb_image='" + thumb_image + '\'' +
                ", karmaPoints=" + karmaPoints +
                ", postCount=" + postCount +
                ", isAdmin=" + isAdmin +
                '}';
    }

    public int getKarmaPoints() {
        return karmaPoints;
    }

    public void setKarmaPoints(int karmaPoints) {
        this.karmaPoints = karmaPoints;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
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

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.major);
        dest.writeString(this.image);
        dest.writeString(this.thumb_image);
        dest.writeInt(this.karmaPoints);
        dest.writeInt(this.postCount);
        dest.writeByte(this.isAdmin ? (byte) 1 : (byte) 0);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.major = in.readString();
        this.image = in.readString();
        this.thumb_image = in.readString();
        this.karmaPoints = in.readInt();
        this.postCount = in.readInt();
        this.isAdmin = in.readByte() != 0;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
