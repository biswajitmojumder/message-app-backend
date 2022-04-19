package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.MessageEntity;
import com.messaging.messagingapp.data.models.bindingModel.MessageBindingModel;
import com.messaging.messagingapp.data.models.viewModel.MessageViewModel;

import java.io.FileNotFoundException;
import java.util.List;

public interface MessageService {
    void sendMessage(MessageBindingModel incomingMessage, String senderUsername) throws FileNotFoundException, IllegalAccessException;
    List<MessageViewModel> loadPageableMessagesForChat(Long chatId, String usernameOfLoggedUser, int pageNum) throws IllegalAccessException, FileNotFoundException;
    void deleteMessageById(Long messageId, String loggedUserUsername) throws NoSuchFieldException, IllegalAccessException;
}
