package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageDao {
    List<Message> getOfflineMessages(Integer destUid);
    void clearOfflineMessages(Integer destUid);
    void putMessage(Message message);
}
