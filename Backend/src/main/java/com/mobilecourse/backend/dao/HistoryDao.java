package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.History;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HistoryDao {

    History getHistory(Integer uid, Integer sid);
    Integer getUserHistoriesCount(Integer uid);

    // 根据页数查看用户浏览历史记录, offset为null时默认为0, size为null时默认选择全部(不管offset为多少)
    List<History> getUserHistories(Integer offset, Integer size, Integer uid);
    void updateHistory(Integer uid, Integer sid);
    void putHistory(Integer uid, Integer sid);
}
