package com.example.tilitili.data;

// Useless Class

public class Activity {
    private String avatar;
    private String username;
    private String post_time;
    private String topic;
    private String cover;
    private String title;
    private int num_share;
    private int num_comment;
    private int num_star;

    public Activity(String avatar, String username, String post_time, String topic, String cover, String title, int num_share, int num_comment, int num_star) {
        this.avatar = avatar;
        this.username = username;
        this.post_time = post_time;
        this.topic = topic;
        this.cover = cover;
        this.title = title;
        this.num_share = num_share;
        this.num_comment = num_comment;
        this.num_star = num_star;
    }
}
