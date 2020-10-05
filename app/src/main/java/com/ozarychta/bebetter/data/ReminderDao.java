package com.ozarychta.bebetter.data;

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

    @Query("SELECT * FROM reminder WHERE id LIKE :id LIMIT 1")
    Reminder findById(Long id);

    @Query("SELECT * FROM reminder WHERE id IN (:reminderIds)")
    List<Reminder> loadAllByIds(int[] reminderIds);

    @Query("SELECT * FROM reminder WHERE challenge_id LIKE :challengeId AND " +
            "enabled = :enabled LIMIT 1")
    Reminder findByChallengeIdAndEnabled(Long challengeId, Boolean enabled);

    @Query("SELECT * FROM reminder WHERE challenge_id LIKE :challengeId LIMIT 1")
    Reminder findByChallengeId(Long challengeId);

    @Query("SELECT * FROM reminder WHERE challenge_id LIKE :challengeId AND " +
            "user_id LIKE :userId LIMIT 1")
    Reminder findByChallengeIdAndUserId(Long challengeId, Long userId);

    @Query("SELECT * FROM reminder WHERE user_id LIKE :userId AND " +
            "enabled = :enabled")
    List<Reminder> findByUserIdAndEnabled(Long userId, Boolean enabled);

    @Insert
    Long insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

}
