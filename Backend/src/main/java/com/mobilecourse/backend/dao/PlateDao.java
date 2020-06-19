package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Plate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlateDao {
    Plate getPlate(Integer pid);
    List<Plate> getAllPlates();
    void putPlate(Plate plate);
    void setPlate(Plate plate);
}
