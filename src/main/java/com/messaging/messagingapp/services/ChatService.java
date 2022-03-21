package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.ChatEntity;

import java.io.FileNotFoundException;

public interface ChatService {
    ChatEntity returnChatById(Long chatId) throws FileNotFoundException;
}
