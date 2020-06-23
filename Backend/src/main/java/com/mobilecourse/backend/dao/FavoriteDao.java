package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Favorite;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FavoriteDao {

    //获取用户对某个投稿的收藏情况
    Favorite getFavorite(Integer uid, Integer sid);
    //获取某个投稿被收藏的次数
    Integer getSubmissionFavoriteCount(Integer sid);
    //获取用户收藏的所有投稿uid
    List<Integer> getUserAllFavorite(Integer offset, Integer size, Integer uid);
    //获取用户收藏个数
    Integer getUserAllFavoriteCount(Integer uid);
    //增加收藏
    void putFavorite(Integer uid, Integer sid);
    //取消收藏
    void deleteFavorite(Integer uid, Integer sid);

    void updateFavorite(Integer uid, Integer sid);
}
