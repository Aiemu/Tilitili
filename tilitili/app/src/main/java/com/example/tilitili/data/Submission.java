package com.example.tilitili.data;

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

    public Submission(int submissionId, Plate plate, int type, String resource, String title, String introduction, long submissionTime, long watchTimes, int likes) {
        this.submissionId = submissionId;
        this.plate = plate;
        this.type = type;
        this.resource = resource;
        this.title = title;
        this.introduction = introduction;
        this.submissionTime = submissionTime;
        this.watchTimes = watchTimes;
        this.likes = likes;
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
        return resource;
    }

    public String getTitle() {
        return title;
    }

    public String getIntroduction() {
        return introduction;
    }

    public long getSubmissionTime() {
        return submissionTime;
    }

    public long getWatchTimes() {
        return watchTimes;
    }

    public int getLikes() {
        return likes;
    }
}
