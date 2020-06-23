package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Submission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubmissionDao {
    //根据投稿的sid获取具体投稿
    Submission getSubmission(Integer sid);
    //添加投稿
    void putSubmission(Submission submission);
    void setSubmission(Submission submission);

    //根据title模糊搜索, 按照时间排序, maxCount为null时默认返回所有结果.
    List<Submission> searchSubmission(String title, Integer maxCount);

    // 获取某个板块的投稿, 若pid为null, 获取所有投稿
    Integer getCount(Integer pid);

    // 获取某些用户的所有投稿个数.
    Integer getCountOfUser(List<Integer> uids);
    // 删除某个投稿.
    void deleteSubmission(Integer sid);

    // 获取多名用户的投稿历史
    List<Submission> getSubmissionHistory(Integer offset, Integer size, List<Integer> uids);
    // 根据页数查看最新和最热(观看次数多)投稿.
    // offset为null时默认为0, size为null时默认选择全部(不管offset为多少), pid为null时默认选择所有板块.
    List<Submission> getSubmissionNewPage(Integer offset, Integer size, Integer pid);
    List<Submission> getSubmissionHotPage(Integer offset, Integer size, Integer pid);

    //增加观看次数.
    void increaseWatchTimes(Integer sid);
}
