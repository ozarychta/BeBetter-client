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

    @ColumnInfo(name = "enabled")
    public Boolean enabled;

    @ColumnInfo(name = "hour")
    public int hour;

    @ColumnInfo(name = "min")
    public int min;

    public Reminder(Long challengeId, Boolean enabled, int hour, int min) {
        this.challengeId = challengeId;
        this.enabled = enabled;
        this.hour = hour;
        this.min = min;
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

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", challengeId=" + challengeId +
                ", enabled=" + enabled +
                ", hour=" + hour +
                ", min=" + min +
                '}';
    }
}
