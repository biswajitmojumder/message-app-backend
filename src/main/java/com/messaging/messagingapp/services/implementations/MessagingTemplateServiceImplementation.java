package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.models.viewModel.MessageViewModel;
import com.messaging.messagingapp.services.MessagingTemplateService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagingTemplateServiceImplementation implements MessagingTemplateService {
    private final SimpMessagingTemplate messagingTemplate;

    public MessagingTemplateServiceImplementation(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void sendMessageToUser(Long chatId, MessageViewModel messageToSend) {
        messagingTemplate.convertAndSend("/queue/chat/" + chatId, messageToSend);
    }

    @Override
    public void sendChatToUser(String username, Long chatId) {
        messagingTemplate.convertAndSend("/queue/chat-list/" + username, chatId);
    }
}
