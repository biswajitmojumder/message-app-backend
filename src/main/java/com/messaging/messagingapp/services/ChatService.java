package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.models.viewModel.ChatListViewModel;
import com.messaging.messagingapp.data.models.viewModel.ChatParticipantViewModel;
import com.messaging.messagingapp.exceptions.ChatNotFoundException;

import java.io.FileNotFoundException;
import java.util.List;

public interface ChatService {
    ChatEntity returnInnerChatById(Long chatId)
            throws FileNotFoundException,
            ChatNotFoundException;
    List<ChatListViewModel> loadChatListOfLoggedUser(String username);
    List<ChatParticipantViewModel> returnParticipantsOfChat(Long chatId, String loggedUserUsername)
            throws IllegalAccessException,
            NoSuchFieldException,
            ChatNotFoundException;
    ChatEntity createNewChat(String loggedUserUsername, String otherUserUsername);
    Boolean doesUserParticipateInChat(String username, Long chatId);
    Boolean doesLoggedUserHaveAChatWithOtherUser(String loggedUserUsername, String otherUserUsername);
}
