package com.example.tilitili.data;

import android.annotation.SuppressLint;

import com.example.tilitili.Config;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Submission {
    private int submissionId;
    private Plate plate;
    private int type;
    private String resource;
    private String title;
    private String introduction;
    private long submissionTime;
    private long watchTimes;
    private int likes;
    private int post_time;

    public Submission(int submissionId, Plate plate, int type, String resource, String title, String introduction, long submissionTime, long watchTimes, int likes, int post_time) {
        this.submissionId = submissionId;
        this.plate = plate;
        this.type = type;
        this.resource = resource;
        this.title = title;
        this.introduction = introduction;
        this.submissionTime = submissionTime;
        this.watchTimes = watchTimes;
        this.likes = likes;
        this.post_time = post_time;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public Plate getPlate() {
        return plate;
    }

    public int getType() {
        return type;
    }

    public String getResource() {
        return Config.getFullUrl(resource);
    }

    public String getTitle() {
        return title;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getSubmissionTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(this.submissionTime * 1000L));
    }

    public long getWatchTimes() {
        return watchTimes;
    }

    public int getLikes() {
        return likes;
    }

    public int getPost_time() {
        return post_time;
    }
}
