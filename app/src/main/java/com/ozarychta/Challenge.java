package com.ozarychta;

import com.ozarychta.enums.AccessType;
import com.ozarychta.enums.Category;
import com.ozarychta.enums.ConfirmationType;
import com.ozarychta.enums.RepeatPeriod;

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

    public Challenge(String title, Category category, RepeatPeriod repeatPeriod, String city, Integer goal) {
        this.title = title;
        this.category = category;
        this.repeatPeriod = repeatPeriod;
        this.city = city;
        this.goal = goal;
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

    @Override
    public String toString() {
        return "Challenge{" +
                "title='" + title + '\'' +
                ", category=" + category +
                ", repeatPeriod=" + repeatPeriod +
                ", city='" + city + '\'' +
                ", goal=" + goal +
                '}';
    }
}
