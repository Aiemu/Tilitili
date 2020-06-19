package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Submission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubmissionDao {
    Submission getSubmission(Integer sid);
    void putSubmission(Submission submission);
    void setSubmission(Submission submission);
}
