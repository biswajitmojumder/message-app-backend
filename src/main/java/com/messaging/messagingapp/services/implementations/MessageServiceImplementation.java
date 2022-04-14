package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.MessageEntity;
import com.messaging.messagingapp.data.models.bindingModel.MessageBindingModel;
import com.messaging.messagingapp.data.models.viewModel.MessageViewModel;
import com.messaging.messagingapp.data.models.viewModel.ReplyMessageViewModel;
import com.messaging.messagingapp.data.repositories.MessageRepository;
import com.messaging.messagingapp.data.repositories.pageableRepositories.PageableMessageRepository;
import com.messaging.messagingapp.services.MessageService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImplementation implements MessageService {
    private final MessageRepository messageRepository;
    private final PageableMessageRepository pageableMessageRepository;
    private final ChatServiceImplementation chatServiceImplementation;
    private final ParticipantServiceImplementation participantServiceImplementation;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageServiceImplementation(MessageRepository messageRepository,
                                        PageableMessageRepository pageableMessageRepository,
                                        ChatServiceImplementation chatServiceImplementation,
                                        ParticipantServiceImplementation participantServiceImplementation,
                                        ModelMapper modelMapper,
                                        SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.pageableMessageRepository = pageableMessageRepository;
        this.chatServiceImplementation = chatServiceImplementation;
        this.participantServiceImplementation = participantServiceImplementation;
        this.modelMapper = modelMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void sendMessage(MessageBindingModel incomingMessage, String senderUsername)
            throws FileNotFoundException,
            IllegalAccessException {
        if(chatServiceImplementation.doesUserParticipateInChat(senderUsername, incomingMessage.getChatId())) {
            MessageEntity message = saveMessage(incomingMessage, senderUsername);
            MessageViewModel messageToSend = new MessageViewModel();
            modelMapper.map(message, messageToSend);
            messageToSend.setSenderUsername(senderUsername);
            if (message.getReplyingTo() != null) {
                ReplyMessageViewModel reply = new ReplyMessageViewModel();
                modelMapper.map(message.getReplyingTo(), reply);
                messageToSend.setReplyTo(reply);
            }
            messageToSend.setChatId(incomingMessage.getChatId());
            messagingTemplate.convertAndSend("/queue/chat/" + incomingMessage.getChatId(), messageToSend);
        }
        else throw new IllegalAccessException();
    }

    @Override
    public List<MessageViewModel> loadPageableMessagesForChat(Long chatId, String usernameOfLoggedUser, int pageNum) throws IllegalAccessException {
        if(chatServiceImplementation.doesUserParticipateInChat(usernameOfLoggedUser, chatId)){
            Pageable page = PageRequest.of(pageNum, 50);
            List<MessageEntity> messages = pageableMessageRepository.getByChat_IdOrderByCreateTimeDesc(chatId, page);
            Collections.reverse(messages);
            List<MessageViewModel> mappedMessages = new ArrayList<>();
            for (MessageEntity message:
                    messages) {
                MessageViewModel mappedMessage = new MessageViewModel();
                modelMapper.map(message, mappedMessage);
                mappedMessage.setSenderNickname(message.getSender().getNickname());
                mappedMessage.setSenderUsername(message.getSender().getUser().getUsername());
                if(message.getReplyingTo() != null){
                    ReplyMessageViewModel reply = new ReplyMessageViewModel();
                    modelMapper.map(message.getReplyingTo(), reply);
                    mappedMessage.setReplyTo(reply);
                }
                mappedMessages.add(mappedMessage);
            }
            return mappedMessages;
        }
        throw new IllegalAccessException();
    }

    private MessageEntity saveMessage(MessageBindingModel incomingMessage, String senderUsername)
            throws FileNotFoundException {
        ChatEntity chat = chatServiceImplementation.returnInnerChatById(incomingMessage.getChatId());
        MessageEntity newMessage = new MessageEntity();
        modelMapper.map(incomingMessage, newMessage);
        newMessage.setChat(chat);
        newMessage.setSender(participantServiceImplementation
                .returnParticipantByChatIdAndUsername(senderUsername, incomingMessage.getChatId()));
        newMessage.setIsSeen(false);
        if (incomingMessage.getMessageReplyId() != null && incomingMessage.getMessageReplyId() != -1){
            Optional<MessageEntity> replyMessageOrNull = messageRepository
                    .findById(incomingMessage.getMessageReplyId());
            replyMessageOrNull.ifPresent(newMessage::setReplyingTo);
        }
        messageRepository.save(newMessage);
        return newMessage;
    }


}
