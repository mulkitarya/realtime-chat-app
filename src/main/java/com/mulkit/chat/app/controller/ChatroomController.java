package com.mulkit.chat.app.controller;

import com.mulkit.chat.app.model.Chatroom;
import com.mulkit.chat.app.service.ChatroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class ChatroomController {

    @Autowired
    private ChatroomService chatroomService;

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody RoomRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Chatroom room = chatroomService.createRoom(request.getName(), email);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestBody RoomRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Chatroom room = chatroomService.joinRoom(request.getName(), email);
        return ResponseEntity.ok(room);
    }

    @GetMapping
    public ResponseEntity<?> getAllRooms(){
        return ResponseEntity.ok(chatroomService.getAllRooms());
    }

    @GetMapping("/my-rooms")
    public ResponseEntity<?> getMyRooms(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(chatroomService.getUserRooms(email));
    }

    static class RoomRequest{
        private String name;
        public String getName(){
            return name;
        }
        public void setName(String name){
            this.name = name;
        }
    }
}
