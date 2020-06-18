package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.PlateAuth;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlateAuthDao {
    List<PlateAuth> getAuth(Integer uid);
    void addAuth(PlateAuth plateAuth);
}
