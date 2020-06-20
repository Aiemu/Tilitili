package com.example.tilitili.data;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.tilitili.Config;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    private int userId;
    private String username;
    private String nickname;
    private String avatar;
    private String bio;
    private long joinAt;
    private String department;
    private String organization;
    private String email;

    public User(int id) {
        this.userId = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getJoinAt() {
        Log.d("joinAr", String.valueOf(this.joinAt));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(this.joinAt));
    }

    public void setJoinAt(long joinAt) {
        this.joinAt = joinAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getFullAvatar() {
        return Config.getFullUrl(avatar);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
