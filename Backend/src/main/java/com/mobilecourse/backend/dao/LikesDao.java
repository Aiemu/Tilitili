package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Likes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LikesDao {
    //获取点赞信息
    Likes getLike(Integer uid, Integer sid);
    //获取投稿的点赞个数
    int getSubmissionLikes(Integer sid);
    //点赞
    void putLike(Integer uid, Integer sid);
    //取消点赞
    void deleteLike(Integer uid, Integer sid);
}
