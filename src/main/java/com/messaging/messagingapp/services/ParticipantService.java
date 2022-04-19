package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.ChatParticipantEntity;

import java.io.FileNotFoundException;
import java.util.List;

public interface ParticipantService {
    ChatParticipantEntity returnParticipantByChatIdAndUsername(String username, Long chatId) throws FileNotFoundException;
    Boolean returnParticipantUnseenMessagesByChatIdAndUsername(String username, Long chatId);
    ChatParticipantEntity createAParticipant(String usernameOfUser, ChatEntity chat);
    List<ChatEntity> returnListOfChatsOfUser(String username);
    void nullUnseenMessagesForParticipantByLoggedUserAndChatId(String loggedUserUsername, Long chatId) throws FileNotFoundException;
}
