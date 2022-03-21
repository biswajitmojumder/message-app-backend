package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.repositories.ChatRepository;
import com.messaging.messagingapp.services.ChatService;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.Optional;

@Service
public class ChatServiceImplementation implements ChatService {
    private final ChatRepository chatRepository;

    public ChatServiceImplementation(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public ChatEntity returnChatById(Long chatId) throws FileNotFoundException {
        Optional<ChatEntity> chatOrNull = chatRepository.findById(chatId);
        if(chatOrNull.isPresent())
            return chatOrNull.get();
        else throw new FileNotFoundException("Chat not found.");
    }
}
