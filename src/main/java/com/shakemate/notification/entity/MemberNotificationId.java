package com.shakemate.notification.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Objects;

@Data
@EqualsAndHashCode
public class MemberNotificationId implements Serializable {
    
    private Integer notification;
    private Integer user;
    
    public MemberNotificationId() {}
    
    public MemberNotificationId(Integer notification, Integer user) {
        this.notification = notification;
        this.user = user;
    }

    public Integer getNotification() { return notification; }
    public void setNotification(Integer notification) { this.notification = notification; }
    public Integer getUser() { return user; }
    public void setUser(Integer user) { this.user = user; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberNotificationId that = (MemberNotificationId) o;
        return Objects.equals(notification, that.notification) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notification, user);
    }
} 