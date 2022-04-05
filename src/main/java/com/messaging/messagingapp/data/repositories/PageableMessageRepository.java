package com.messaging.messagingapp.data.repositories;

import com.messaging.messagingapp.data.entities.MessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageableMessageRepository extends PagingAndSortingRepository<MessageEntity, Long> {
    List<MessageEntity> getByChat_IdOrderByCreateTimeDesc(Long chatId, Pageable pageable);
}
