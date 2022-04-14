package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.models.viewModel.ChatListViewModel;
import com.messaging.messagingapp.data.models.viewModel.ChatMessagesViewModel;

import java.io.FileNotFoundException;
import java.util.List;

public interface ChatService {
    ChatEntity returnInnerChatById(Long chatId) throws FileNotFoundException;
    List<ChatListViewModel> loadChatListOfLoggedUser(String username);
    ChatEntity createNewChat(String loggedUserUsername, String otherUserUsername);
    Boolean doesUserParticipateInChat(String username, Long chatId);
    Boolean doesLoggedUserHaveAChatWithOtherUser(String loggedUserUsername, String otherUserUsername);
}
