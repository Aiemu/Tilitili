package com.example.tilitili.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.tilitili.dao.MessageDao;

@Database(entities = {Message.class}, version = 1, exportSchema = false)
public abstract class MessageDatabase extends RoomDatabase {

    private static MessageDatabase databaseInstance;

    public static synchronized MessageDatabase getInstance(Context context) {
        if (databaseInstance == null) {
            databaseInstance = Room
                    .databaseBuilder(context.getApplicationContext(), MessageDatabase.class, Contants.SQLITE_DATABASE_NAME)
                    .build();
        }
        return databaseInstance;
    }

    public abstract MessageDao messageDao();
}
