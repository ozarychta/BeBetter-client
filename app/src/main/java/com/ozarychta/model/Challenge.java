package com.ozarychta.model;

import com.ozarychta.enums.AccessType;
import com.ozarychta.enums.Category;
import com.ozarychta.enums.ChallengeState;
import com.ozarychta.enums.ConfirmationType;
import com.ozarychta.enums.RepeatPeriod;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Challenge implements Serializable {

    private Long id;

    private String title;

    private String description;

    private AccessType accessType;

    private Category category;

    private RepeatPeriod repeatPeriod;

    private String city;

    private Date startDate;

    private Date endDate;

    private ChallengeState state;

    private ConfirmationType confirmationType;

    private Boolean done;

    private Integer goal;

    private Boolean isUserParticipant;

    private Integer creatorId;

    public Challenge() {
    }

    public Challenge(String title, Category category, RepeatPeriod repeatPeriod, String city, Integer goal) {
        this.title = title;
        this.category = category;
        this.repeatPeriod = repeatPeriod;
        this.city = city;
        this.goal = goal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ChallengeState getState() {
        return state;
    }

    public void setState(ChallengeState state) {
        this.state = state;
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

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public Boolean isUserParticipant() {
        return isUserParticipant;
    }

    public void setUserParticipant(Boolean userParticipant) {
        isUserParticipant = userParticipant;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", accessType=" + accessType +
                ", category=" + category +
                ", repeatPeriod=" + repeatPeriod +
                ", city='" + city + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", state=" + state +
                ", confirmationType=" + confirmationType +
                ", done=" + done +
                ", goal=" + goal +
                ", isUserParticipant=" + isUserParticipant +
                ", creatorId=" + creatorId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return Objects.equals(id, challenge.id) &&
                Objects.equals(title, challenge.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
