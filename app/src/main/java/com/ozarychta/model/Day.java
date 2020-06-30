package com.ozarychta.model;

import com.ozarychta.enums.ConfirmationType;

import java.io.Serializable;
import java.util.Date;

public class Day implements Serializable {
    private Long id;

    private Date date;

    private Boolean done;

    private Integer currentStatus;

    private ConfirmationType confirmationType;

    public Day(Long id, Date date, Boolean done, Integer currentStatus) {
        this.id = id;
        this.date = date;
        this.done = done;
        this.currentStatus = currentStatus;
        this.confirmationType = ConfirmationType.CHECK_TASK;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public ConfirmationType getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(ConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
    }
}
