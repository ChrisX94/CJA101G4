package com.shakemate.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class MailService {

    @Value("${mail.username}")
    private String mailUsername;

    @Value("${mail.password}")
    private String mailPassword;

    public void sendMail(String to, String subject, String content) {
        SSLBypass.disableSSLVerification();
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailUsername, mailPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("寄信失敗", e);
        }
    }

    // 專用：註冊驗證信
    public void sendVerificationEmail(String to, String username, String verifyLink) {
        String subject = "帳號驗證信";
        String content = "Hello " + username + "，\n\n請點選以下連結完成帳號驗證：\n" + verifyLink;
        sendMail(to, subject, content);
    }

    // 專用：忘記密碼信
    public void sendResetPasswordEmail(String to, String resetLink) {

        String subject = "重設密碼通知";
        String content = "您好，\n\n請點選以下連結重新設定您的密碼（連結 5 分鐘內有效）：\n\n" + resetLink + "\n\n" +
                "如果您沒有請求此重設，請忽略本信件。\n\nShakemate團隊敬上";
        sendMail(to, subject, content);
    }

}
