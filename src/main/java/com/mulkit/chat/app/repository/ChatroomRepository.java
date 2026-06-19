package com.mulkit.chat.app.repository;

import com.mulkit.chat.app.model.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
    Chatroom findByName(String name);
    List<Chatroom> findByMembersEmail(String email);
}
