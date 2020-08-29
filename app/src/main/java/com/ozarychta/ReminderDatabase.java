package com.ozarychta;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Reminder.class}, version = 1)
public abstract class ReminderDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "reminder_db";
    private static ReminderDatabase instance;

    public static synchronized ReminderDatabase getInstance(Context ctx){
        if(instance == null){
            instance = Room.databaseBuilder(ctx.getApplicationContext(),
                    ReminderDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract ReminderDao reminderDao();
}
