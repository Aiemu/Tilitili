package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentDao {
    int getCommentCounts(Integer sid);
    List<Comment> getAllComments(Integer sid);
    List<Comment> getCommentPage(Integer offset, Integer size, Integer sid);
    void putComment(Comment comment);
    void deleteComment(Integer cid);
}
