package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageDao {
    //获取发送到destUid的所有离线信息
    List<Message> getOfflineMessages(Integer destUid);
    //根据mid获取信息
    Message getMessage(Integer mid);
    //清空发到destUid的所有离线信息
    void clearOfflineMessages(Integer destUid);
    //添加离线信息
    void putMessage(Message message);
}
