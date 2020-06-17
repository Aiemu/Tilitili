package com.example.tilitili.data;

public class Submission {
    private int submissionId;
    private int plateId;
    private int type;
    private String resource;
    private String title;
    private String introduction;
    //    private String snapshot;
    private long submissionTime;
    private long watchTimes;

    public Submission(int submissionId, int plateId, int type, String resource, String title, String introduction, long submissionTime, long watchTimes) {
        this.submissionId = submissionId;
        this.plateId = plateId;
        this.type = type;
        this.resource = resource;
        this.title = title;
        this.introduction = introduction;
        this.submissionTime = submissionId;
        this.watchTimes = watchTimes;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public int getPlateId() {
        return plateId;
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
}
