package com.ozarychta.bebetter.models;

import java.io.Serializable;

public class Achievement implements Serializable {

    private Long id;

    private String title;

    private String description;

    private Boolean achieved;

    public Achievement(Long id, String title, String desc, Boolean achieved) {
        this.id = id;
        this.title = title;
        this.description = desc;
        this.achieved = achieved;
    }

    public Achievement(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAchieved() {
        return achieved;
    }

    public void setAchieved(Boolean achieved) {
        this.achieved = achieved;
    }
}
