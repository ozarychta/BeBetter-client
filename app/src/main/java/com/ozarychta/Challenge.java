package com.ozarychta;

import java.util.Date;

public class Challenge {

    private String title;

    private String description;

    private AccessType accessType;

    private Category category;

    private RepeatPeriod repeatPeriod;

    private String city;

    private Date startDate;

    private Date endDate;

    private Boolean active;

    private ConfirmationType confirmationType;

    private Boolean done;

    private Integer goal;

    private Boolean isMoreBetter;

    private Integer creatorId;

    public Challenge() {
    }

    public Challenge(String title, String description, AccessType accessType, Category category, RepeatPeriod repeatPeriod, String city, Date startDate, Date endDate, Boolean active, ConfirmationType confirmationType, Boolean done, Integer goal, Boolean isMoreBetter, Integer creatorId) {
        this.title = title;
        this.description = description;
        this.accessType = accessType;
        this.category = category;
        this.repeatPeriod = repeatPeriod;
        this.city = city;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.confirmationType = confirmationType;
        this.done = done;
        this.goal = goal;
        this.isMoreBetter = isMoreBetter;
        this.creatorId = creatorId;
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

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public RepeatPeriod getRepeatPeriod() {
        return repeatPeriod;
    }

    public void setRepeatPeriod(RepeatPeriod repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ConfirmationType getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(ConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public Boolean getMoreBetter() {
        return isMoreBetter;
    }

    public void setMoreBetter(Boolean moreBetter) {
        isMoreBetter = moreBetter;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", accessType=" + accessType +
                ", category=" + category +
                ", repeatPeriod=" + repeatPeriod +
                ", city='" + city + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", active=" + active +
                ", confirmationType=" + confirmationType +
                ", done=" + done +
                ", goal=" + goal +
                ", isMoreBetter=" + isMoreBetter +
                ", creatorId=" + creatorId +
                '}';
    }
}
