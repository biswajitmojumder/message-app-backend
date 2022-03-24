package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.ChatParticipantEntity;
import com.messaging.messagingapp.data.entities.UserEntity;

import java.io.FileNotFoundException;

public interface ParticipantService {
    ChatParticipantEntity returnParticipantById(Long id);
    ChatParticipantEntity returnParticipantByChatIdAndUsername(String username, Long chatId) throws FileNotFoundException;
    ChatParticipantEntity createAParticipant(String usernameOfUser, ChatEntity chat);
}
