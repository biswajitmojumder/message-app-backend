package com.messaging.messagingapp.data.repositories;

import com.messaging.messagingapp.data.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    @Query("select m from MessageEntity m where m.chat.id = ?1 order by m.createTime asc ")
    List<MessageEntity> last50MessagesOfAChat(Long chatId);
}
