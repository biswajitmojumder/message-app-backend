package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.models.viewModel.ChatListViewModel;
import com.messaging.messagingapp.data.models.viewModel.ChatMessagesViewModel;

import java.io.FileNotFoundException;
import java.util.List;

public interface ChatService {
    ChatEntity returnInnerChatById(Long chatId) throws FileNotFoundException;
    ChatMessagesViewModel returnOuterChatById(Long chatId, String loggedUserUsername);
    List<ChatListViewModel> loadChatListOfLoggedUser(String username);
    void createNewChat(String loggedUserUsername, Long otherUserId);
}
