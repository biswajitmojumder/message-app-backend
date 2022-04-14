package com.messaging.messagingapp.data.repositories;

import com.messaging.messagingapp.data.entities.ChatParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<ChatParticipantEntity, Long> {
    Optional<ChatParticipantEntity> findByChat_IdAndUser_Username(Long chatId, String username);
    List<ChatParticipantEntity> getAllByUser_Username(String username);
}
