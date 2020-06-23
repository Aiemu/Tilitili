package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Favorite;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FavoriteDao {

    Favorite getFavorite(Integer uid, Integer sid);
    Integer getSubmissionFavoriteCount(Integer sid);
    List<Integer> getUserAllFavorite(Integer offset, Integer size, Integer uid);
    Integer getUserAllFavoriteCount(Integer uid);
    void putFavorite(Integer uid, Integer sid);
    void deleteFavorite(Integer uid, Integer sid);
    void updateFavorite(Integer uid, Integer sid);
}
