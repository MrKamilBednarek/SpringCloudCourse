package com.kamil.notifications.service;

import com.kamil.notifications.dto.EmailDto;
import com.kamil.notifications.dto.NotificationInfoDto;

import javax.mail.MessagingException;

public interface EmailSender {
    void sendEmails(NotificationInfoDto notificationInfo);


    void sendEmail(EmailDto emailDto) throws MessagingException;
}

