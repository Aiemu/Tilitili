package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Comment;
import com.mobilecourse.backend.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentDao {
    // 根据投稿的sid获取评论个数
    int getCommentCounts(Integer sid);
    // 根据投稿的sid获取所有的评论
    List<Comment> getAllComments(Integer sid);
    // 根据页数查看某个投稿下的评论, offset为null时默认为0, size为null时默认选择全部(不管offset为多少)
    List<Comment> getCommentPage(Integer offset, Integer size, Integer sid);
    // 加入一条评论到数据库中
    void putComment(Comment comment);
    // 删除一条评论
    void deleteComment(Integer cid);
}
