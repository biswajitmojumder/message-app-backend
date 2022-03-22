package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.MessageEntity;
import com.messaging.messagingapp.data.models.bindingModel.MessageBindingModel;
import com.messaging.messagingapp.data.models.viewModel.MessageViewModel;
import com.messaging.messagingapp.data.repositories.MessageRepository;
import com.messaging.messagingapp.services.MessageService;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.Optional;

@Service
public class MessageServiceImplementation implements MessageService {
    private final MessageRepository messageRepository;
    private final ChatServiceImplementation chatServiceImplementation;
    private final ParticipantServiceImplementation participantServiceImplementation;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageServiceImplementation(MessageRepository messageRepository,
                                        ChatServiceImplementation chatServiceImplementation,
                                        ParticipantServiceImplementation participantServiceImplementation,
                                        ModelMapper modelMapper,
                                        SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.chatServiceImplementation = chatServiceImplementation;
        this.participantServiceImplementation = participantServiceImplementation;
        this.modelMapper = modelMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void sendMessage(MessageBindingModel incomingMessage, String senderUsername) throws FileNotFoundException {
        saveMessage(incomingMessage, senderUsername);
        messagingTemplate.convertAndSend("/queue/chat/" + incomingMessage.getChatId(), incomingMessage);
    }

    private void saveMessage(MessageBindingModel incomingMessage, String senderUsername) throws FileNotFoundException {
        ChatEntity chat = chatServiceImplementation.returnInnerChatById(incomingMessage.getChatId());
        MessageEntity newMessage = new MessageEntity();
        modelMapper.map(incomingMessage, newMessage);
        newMessage.setChat(chat);
        newMessage.setSender(participantServiceImplementation
                .returnParticipantByChatIdAndUsername(senderUsername, incomingMessage.getChatId()));
        newMessage.setIsSeen(false);
        if (incomingMessage.getMessageReplyId() != null){
            Optional<MessageEntity> replyMessageOrNull = messageRepository
                    .findById(incomingMessage.getMessageReplyId());
            replyMessageOrNull.ifPresent(newMessage::setReplyingTo);
        }
        messageRepository.save(newMessage);
    }
}
