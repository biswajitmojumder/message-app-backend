package com.messaging.messagingapp.data.repositories;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.ChatParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    @Query("select c.participants from ChatEntity c where c.id = ?1")
    List<ChatParticipantEntity> returnParticipantsOfChat(Long chatId);
}
