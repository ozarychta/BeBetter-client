package com.ozarychta;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReminderDao {
    @Query("SELECT * FROM reminder")
    List<Reminder> getAll();

    @Query("SELECT * FROM reminder WHERE id IN (:reminderIds)")
    List<Reminder> loadAllByIds(int[] reminderIds);

    @Query("SELECT * FROM reminder WHERE challenge_id LIKE :challengeId AND " +
            "enabled = :enabled LIMIT 1")
    Reminder findByChallengeIdAndEnabled(Long challengeId, Boolean enabled);

    @Query("SELECT * FROM reminder WHERE challenge_id LIKE :challengeId LIMIT 1")
    Reminder findByChallengeId(Long challengeId);

    @Insert
    Long insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

}
