package com.test.nba.nbaInfo.service;

import com.test.nba.nbaInfo.domain.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WSService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WSService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyClient(final String message) {
        ResponseEntity<String> response = new ResponseEntity<>(message, HttpStatus.OK);
        messagingTemplate.convertAndSend("/topic/messages", response);
        //messagingTemplate.convertAndSend("http://localhost:8082/messages", response);
    }

}
