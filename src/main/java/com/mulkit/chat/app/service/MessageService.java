package com.mulkit.chat.app.service;

import com.mulkit.chat.app.model.Chatroom;
import com.mulkit.chat.app.model.Message;
import com.mulkit.chat.app.model.User;
import com.mulkit.chat.app.repository.ChatroomRepository;
import com.mulkit.chat.app.repository.MessageRepository;
import com.mulkit.chat.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatroomRepository chatroomRepository;

    public Message saveMessage(String content, String senderEmail, Long roomId){
        User sender = userRepository.findByEmail(senderEmail);
        Chatroom room = chatroomRepository.findById(roomId)
                .orElseThrow(()-> new RuntimeException("Room not found"));
        Message message = new Message();
        message.setContent(content);
        message.setSender(sender.getUsername());
        message.setChatroom(room);
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public List<Message> getRoomMessages(Long roomId){
        return messageRepository.findByChatroomIdOrderByTimestampAsc(roomId);
    }
}
