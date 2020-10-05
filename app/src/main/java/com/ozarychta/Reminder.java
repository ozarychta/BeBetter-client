package com.ozarychta;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "challenge_id")
    public Long challengeId;

    @ColumnInfo(name = "user_id")
    public Long userId;

    @ColumnInfo(name = "enabled")
    public Boolean enabled;

    @ColumnInfo(name = "hour")
    public int hour;

    @ColumnInfo(name = "min")
    public int min;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "end_date")
    public String endDate;

    public Reminder(Long challengeId, Long userId, Boolean enabled, int hour, int min, String title, String endDate) {
        this.challengeId = challengeId;
        this.userId = userId;
        this.enabled = enabled;
        this.hour = hour;
        this.min = min;
        this.title = title;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", challengeId=" + challengeId +
                ", userId=" + userId +
                ", enabled=" + enabled +
                ", hour=" + hour +
                ", min=" + min +
                ", title='" + title + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
