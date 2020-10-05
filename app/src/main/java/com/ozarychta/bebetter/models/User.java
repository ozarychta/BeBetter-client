package com.ozarychta.bebetter.models;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private Long id;

    private String username;

    private String aboutMe;

    private String mainGoal;

    private Integer rankingPoints;

    private Integer highestStrike;

    private Boolean followed;

    public User() {
    }

    public User(Long id, String username, String aboutMe, String mainGoal, Integer rankingPoints, Integer highestStrike, Boolean followed) {
        this.id = id;
        this.username = username;
        this.aboutMe = aboutMe;
        this.mainGoal = mainGoal;
        this.rankingPoints = rankingPoints;
        this.highestStrike = highestStrike;
        this.followed = followed;
    }

    public User(Long id, String username, String aboutMe, String mainGoal, Integer rankingPoints, Integer highestStrike) {
        this.id = id;
        this.username = username;
        this.aboutMe = aboutMe;
        this.mainGoal = mainGoal;
        this.rankingPoints = rankingPoints;
        this.highestStrike = highestStrike;
        this.followed = false;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getMainGoal() {
        return mainGoal;
    }

    public void setMainGoal(String mainGoal) {
        this.mainGoal = mainGoal;
    }

    public Integer getRankingPoints() {
        return rankingPoints;
    }

    public void setRankingPoints(Integer rankingPoints) {
        this.rankingPoints = rankingPoints;
    }

    public Integer getHighestStrike() {
        return highestStrike;
    }

    public void setHighestStrike(Integer highestStrike) {
        this.highestStrike = highestStrike;
    }

    public Boolean isFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(aboutMe, user.aboutMe) &&
                Objects.equals(mainGoal, user.mainGoal) &&
                Objects.equals(rankingPoints, user.rankingPoints) &&
                Objects.equals(highestStrike, user.highestStrike) &&
                Objects.equals(followed, user.followed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, aboutMe, mainGoal, rankingPoints, highestStrike, followed);
    }
}
