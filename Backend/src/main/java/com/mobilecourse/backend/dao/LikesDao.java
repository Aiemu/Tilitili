package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Likes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LikesDao {
    Likes getLike(Integer uid, Integer sid);
    int getSubmissionLikes(Integer sid);
    void putLike(Integer uid, Integer sid);
    void deleteLike(Integer uid, Integer sid);
}
