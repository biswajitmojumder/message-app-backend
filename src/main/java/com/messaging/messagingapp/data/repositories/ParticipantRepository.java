package com.messaging.messagingapp.data.repositories;

import com.messaging.messagingapp.data.entities.ChatParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<ChatParticipantEntity, Long> {
    Optional<ChatParticipantEntity> findByChat_IdAndUser_Username(Long chatId, String username);
    List<ChatParticipantEntity> getAllByUser_Username(String username);
    @Query("select p.unseenMessages from ChatParticipantEntity p where p.chat.id = ?1 and p.user.username = ?2")
    Boolean getUnseenMessagesByChatIdAndUsername(Long chatId, String username);
}
