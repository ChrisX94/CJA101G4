package com.shakemate.notification.service;

import com.shakemate.notification.entity.MemberNotification;
import com.shakemate.notification.enums.DeliveryStatus;
import com.shakemate.notification.repository.MemberNotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final MemberNotificationRepository memberNotificationRepository;
    private final String fromEmail;

    public static final int MAX_RETRIES = 3;
    public static final long RETRY_DELAY_MS = 5000;

    @Autowired
    public EmailServiceImpl(
            JavaMailSender mailSender,
            MemberNotificationRepository memberNotificationRepository,
            @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.memberNotificationRepository = memberNotificationRepository;
        this.fromEmail = fromEmail;
    }

    @Override
    @Async("taskExecutor")
    public void sendHtmlEmail(MemberNotification memberNotification, String subject, String htmlBody) throws MailException {
        String to = memberNotification.getUser().getEmail();
        
        // 立即標記為嘗試中
        updateNotificationStatus(memberNotification, DeliveryStatus.RETRYING, null);

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setFrom(fromEmail);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlBody, true);
                mailSender.send(mimeMessage);

                log.info("郵件在第 {} 次嘗試成功發送至 {}。通知ID: {}", attempt, to, memberNotification.getNotification().getNotificationId());
                // 成功後更新狀態並直接返回
                updateNotificationStatus(memberNotification, DeliveryStatus.SUCCESS, null);
                return;

            } catch (MailException | MessagingException e) {
                log.warn("郵件發送至 {} 第 {}/{} 次嘗試失敗。錯誤: {}", to, attempt, MAX_RETRIES, e.getMessage());

                if (attempt == MAX_RETRIES) {
                    String errorMessage = "經過 " + MAX_RETRIES + " 次重試後最終失敗: " + e.getMessage();
                    // 截斷錯誤訊息
                    if (errorMessage.length() > 400) {
                        errorMessage = errorMessage.substring(0, 400);
                    }
                    log.error(errorMessage, e);
                    updateNotificationStatus(memberNotification, DeliveryStatus.FAILED, errorMessage);
                } else {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        log.error("重試等待期間執行緒被中斷", ie);
                        Thread.currentThread().interrupt();
                        updateNotificationStatus(memberNotification, DeliveryStatus.FAILED, "重試等待時被中斷");
                        break;
                    }
                }
            }
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateNotificationStatus(MemberNotification memberNotification, DeliveryStatus status, String errorMessage) {
        // 使用複合主鍵查找
        memberNotificationRepository.findByUser_UserIdAndNotification_NotificationId(
            memberNotification.getUser().getUserId(), 
            memberNotification.getNotification().getNotificationId()
        ).ifPresent(notification -> {
            notification.setDeliveryStatus(status);
            notification.setErrorMessage(errorMessage); // 記錄成功或失敗的訊息
            memberNotificationRepository.save(notification);
        });
    }
} 