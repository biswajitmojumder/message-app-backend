package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.ChatParticipantEntity;
import com.messaging.messagingapp.exceptions.ChatNotFoundException;
import com.messaging.messagingapp.exceptions.UserNotFoundException;

import java.io.FileNotFoundException;
import java.util.List;

public interface ParticipantService {
    ChatParticipantEntity returnParticipantByChatIdAndUsername(String username, Long chatId)
            throws FileNotFoundException, ChatNotFoundException;
    Boolean returnParticipantUnseenMessagesByChatIdAndUsername(String username, Long chatId);
    ChatParticipantEntity createAParticipant(String usernameOfUser, ChatEntity chat) throws UserNotFoundException;
    List<ChatEntity> returnListOfChatsOfUser(String username);
    void nullUnseenMessagesForParticipantByLoggedUserAndChatId(String loggedUserUsername, Long chatId)
            throws FileNotFoundException, ChatNotFoundException;
    void switchUnseenMessagesForAllParticipantsOfAChat(Long chatId) throws ChatNotFoundException;
    void changeNicknameOfParticipantByChatIdAndUsername(String newNickname, Long chatId, String username)
            throws ChatNotFoundException;
    void closeChatForSingleUser(Long chatId, String loggedUserUsername) throws ChatNotFoundException;
    void openChatForSingleUser(Long chatId, String loggedUserUsername) throws ChatNotFoundException;
    void openChatForParticipant(Long chatId, ChatParticipantEntity participant);
}
