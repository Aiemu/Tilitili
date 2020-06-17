package com.example.tilitili.data;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    private int userId;
    private String username;
    private String nickname;
    private int privilege;
    private String bio;
    private long joinAt;
    private String department;
    private String organization;

    private String email;

    public User(int id, String username, int privilege, String nickname) {
        this.userId = id;
        this.username = username;
        this.privilege = privilege;
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

    public int getPrivilege() {
        return privilege;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

//    public int getJoinAt() {
//        return joinAt;
//    }

    public String getJoinAt() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(this.joinAt * 1000L));
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
}
