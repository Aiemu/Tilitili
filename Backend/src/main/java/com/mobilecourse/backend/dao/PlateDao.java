package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Plate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlateDao {
    //根据板块pid获取板块的基本信息
    Plate getPlate(Integer pid);
    //获取所有板块
    List<Plate> getAllPlates();
    //添加板块
    void putPlate(Plate plate);
    //更新板块
    void updatePlate(Plate plate);
}
