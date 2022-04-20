package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.models.viewModel.MessageViewModel;

public interface MessagingTemplateService {
    void sendMessageToUser(Long chatId, MessageViewModel messageToSend);
    void sendChatToUser(String username, Long chatId);
}
