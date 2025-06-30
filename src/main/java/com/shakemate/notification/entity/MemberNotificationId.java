package com.shakemate.notification.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
} 