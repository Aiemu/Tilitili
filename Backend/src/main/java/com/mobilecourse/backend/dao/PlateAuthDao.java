package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.PlateAuth;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlateAuthDao {
    List<PlateAuth> getAuths(Integer uid);
    Integer getAuth(Integer uid, Integer pid);
    void putAuth(PlateAuth plateAuth);
    void setAuth(PlateAuth plateAuth);
}
