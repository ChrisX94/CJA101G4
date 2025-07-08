package com.shakemate.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.shakemate.notification.entity.MemberNotification;
import org.springframework.mail.MailException;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    // 暫時簡化的EmailService實現
    
    @Override
    public void sendHtmlEmail(MemberNotification memberNotification, String subject, String htmlBody) throws MailException {
        try {
            log.info("[模擬] 發送Email給用戶ID: {}，主旨: {}，內容: {}", memberNotification.getUser(), subject, htmlBody);
            // TODO: 實際郵件發送邏輯
        } catch (Exception e) {
            log.error("Email發送失敗: {}", e.getMessage());
            throw new MailException("Email發送失敗: " + e.getMessage()) {};
        }
    }
} 