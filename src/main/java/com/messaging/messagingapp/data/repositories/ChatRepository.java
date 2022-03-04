package com.messaging.messagingapp.data.repositories;

import com.messaging.messagingapp.data.entities.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
}
