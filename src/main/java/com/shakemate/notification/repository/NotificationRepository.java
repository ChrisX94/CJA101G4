package com.shakemate.notification.repository;

import com.shakemate.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
} 