package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentDao {
    int getCommentCounts(Integer sid);
    List<Comment> getAllComments(Integer sid);

    // 根据页数查看某个投稿下的评论, offset为null时默认为0, size为null时默认选择全部(不管offset为多少)
    List<Comment> getCommentPage(Integer offset, Integer size, Integer sid);
    void putComment(Comment comment);
    void deleteComment(Integer cid);
}
