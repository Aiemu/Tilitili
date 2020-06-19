package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Submission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubmissionDao {
    Submission getSubmission(Integer sid);
    void putSubmission(Submission submission);
    void setSubmission(Submission submission);
    Integer getCount(Integer pid);
    void deleteSubmission(Integer sid);
    List<Submission> getSubmissionNewPage(Integer offset, Integer size, Integer pid);
    List<Submission> getSubmissionHotPage(Integer offset, Integer size, Integer pid);
    void increaseWatchTimes(Integer sid);
}
