package com.example.tilitili.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tilitili.data.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(Message message);

    @Insert
    void insertAll(List<Message> messages);

    @Query("SELECT * FROM Message")
    List<Message> getAllMessage();

    @Query("SELECT DISTINCT uid, nickname, avatar, id, content, messageTime, isRead from Message GROUP BY uid")
    List<Message> getAllDistinctUsers();

    @Query("SELECT * from Message where uid = :id")
    List<Message> getOneUserMessage(int id);

    @Delete
    void delete(Message message);

    @Delete
    void deleteAll(Message... messages);

    @Query("SELECT COUNT(*) FROM MESSAGE")
    int getSum();

}