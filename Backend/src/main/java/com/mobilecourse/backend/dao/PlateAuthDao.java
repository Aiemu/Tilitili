package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.PlateAuth;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlateAuthDao {
    List<Integer> getSubmissionAuths(Integer uid);
    void putAuth(PlateAuth plateAuth);
    void deleteAuth(PlateAuth plateAuth);
}
