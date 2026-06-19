package com.mulkit.chat.app.repository;

import com.mulkit.chat.app.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatroomIdOrderByTimestampAsc(Long roomId);
}
