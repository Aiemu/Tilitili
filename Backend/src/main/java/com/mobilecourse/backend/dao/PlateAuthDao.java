package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.PlateAuth;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlateAuthDao {
    //获取用户拥有投稿权限的板块pid
    List<Integer> getSubmissionAuths(Integer uid);
    //添加用户对某个板块的权限(需要进行申请操作)
    void putAuth(PlateAuth plateAuth);
    //移除用户对某个板块的权限(需要进行申请操作)
    void deleteAuth(PlateAuth plateAuth);
}
