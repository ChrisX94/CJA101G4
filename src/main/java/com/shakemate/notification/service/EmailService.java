package com.shakemate.notification.service;

import com.shakemate.notification.entity.MemberNotification;
import org.springframework.mail.MailException;

public interface EmailService {

    /**
     * 發送一封 HTML 格式的電子郵件。
     *
     * @param memberNotification 會員通知實體，包含收件人等資訊
     * @param subject 郵件主旨
     * @param htmlBody 郵件內容 (HTML 格式)
     * @throws MailException 如果郵件發送失敗，將拋出此異常，以便觸發重試
     */
    void sendHtmlEmail(MemberNotification memberNotification, String subject, String htmlBody) throws MailException;
} 