package com.mulkit.chat.app.service;

import com.mulkit.chat.app.model.Chatroom;
import com.mulkit.chat.app.model.User;
import com.mulkit.chat.app.repository.ChatroomRepository;
import com.mulkit.chat.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatroomService {

   @Autowired
    private ChatroomRepository chatroomRepository;

   @Autowired
    private UserRepository userRepository;

   public Chatroom createRoom(String name, String creatorEmail){
       User creator = userRepository.findByEmail(creatorEmail);
       Chatroom room = new Chatroom();
       room.setName(name);
       room.setCreatedBy(creatorEmail);
       room.getMembers().add(creator);
       return chatroomRepository.save(room);
   }

   public Chatroom joinRoom(String roomName, String userEmail){
       Chatroom room = chatroomRepository.findByName(roomName);
       User user = userRepository.findByEmail(userEmail);
       room.getMembers().add(user);
       return chatroomRepository.save(room);
   }

   public List<Chatroom> getUserRooms(String email){
       return chatroomRepository.findByMembersEmail(email);
   }

   public List<Chatroom> getAllRooms(){
       return chatroomRepository.findAll();
   }
}
