package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.ChatParticipantEntity;

import java.io.FileNotFoundException;

public interface ParticipantService {
    ChatParticipantEntity returnParticipantById(Long id);
    ChatParticipantEntity returnParticipantByChatIdAndUsername(String username, Long chatId) throws FileNotFoundException;
    void changeParticipantNickname(Long id, String newNickname);
}
