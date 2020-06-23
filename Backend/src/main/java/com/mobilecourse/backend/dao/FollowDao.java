package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Follow;

import java.util.List;

public interface FollowDao {
    //获取关注信息
    Follow getFollow(Integer followerUid, Integer followedUid);
    //获取粉丝
    List<Integer> getFollowers(Integer followedUid); //获取关注这个人的所有人
    //获取所有关注
    List<Integer> getFolloweds(Integer followerUid); //获取所有关注的人
    //加入关注
    void putFollow(Integer followerUid, Integer followedUid);
    //取消关注
    void deleteFollow(Integer followerUid, Integer followedUid);
}
