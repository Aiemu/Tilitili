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

    @Query("SELECT DISTINCT uid, receiver, nickname, avatar, id, content, messageTime, isRead from Message WHERE uid != :host GROUP BY uid")
    List<Message> getAllDistinctUsers(int host);

    @Query("SELECT * from Message where (uid = :id AND receiver = :receiver) OR (uid = :receiver AND receiver = :id) ORDER BY messageTime ASC")
    List<Message> getOneUserMessage(int id, int receiver);

    @Delete
    void delete(Message message);

    @Delete
    void deleteAll(Message... messages);

    @Query("SELECT COUNT(*) FROM MESSAGE")
    int getSum();

    @Query("UPDATE Message SET isRead = 1 where uid = :id")
    int setRead(int id);

    @Query("SELECT COUNT(*) from Message where uid = :id AND isRead = 0")
    int getUnread(int id);

}