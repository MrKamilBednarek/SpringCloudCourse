package com.kamil.receiver.controller;

import com.kamil.receiver.model.Notification;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
@RabbitListener(queues = "kurs")
    public void listenerMessage(Notification notification){
    System.out.println(notification.getEmail()+" "+notification.getTitle()+" "+notification.getBody());
}
}
