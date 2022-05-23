package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.ChatParticipantEntity;
import com.messaging.messagingapp.data.entities.MessageEntity;
import com.messaging.messagingapp.data.models.bindingModel.MessageBindingModel;
import com.messaging.messagingapp.data.models.viewModel.MessageViewModel;
import com.messaging.messagingapp.data.models.viewModel.ReplyMessageViewModel;
import com.messaging.messagingapp.data.repositories.MessageRepository;
import com.messaging.messagingapp.data.repositories.pageableRepositories.PageableMessageRepository;
import com.messaging.messagingapp.exceptions.ChatNotFoundException;
import com.messaging.messagingapp.exceptions.UserNotFoundException;
import com.messaging.messagingapp.services.MessageService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageServiceImplementation implements MessageService {
    private final MessageRepository messageRepository;
    private final PageableMessageRepository pageableMessageRepository;
    private final ChatServiceImplementation chatServiceImplementation;
    private final ParticipantServiceImplementation participantServiceImplementation;
    private final MessagingTemplateServiceImplementation templateServiceImplementation;
    private final ModelMapper modelMapper;

    public MessageServiceImplementation(MessageRepository messageRepository,
                                        PageableMessageRepository pageableMessageRepository,
                                        ChatServiceImplementation chatServiceImplementation,
                                        ParticipantServiceImplementation participantServiceImplementation,
                                        MessagingTemplateServiceImplementation templateServiceImplementation,
                                        ModelMapper modelMapper) {
        this.messageRepository = messageRepository;
        this.pageableMessageRepository = pageableMessageRepository;
        this.chatServiceImplementation = chatServiceImplementation;
        this.participantServiceImplementation = participantServiceImplementation;
        this.templateServiceImplementation = templateServiceImplementation;
        this.modelMapper = modelMapper;
    }

    @Override
    public void sendMessage(MessageBindingModel incomingMessage, String senderUsername)
            throws IllegalAccessException, ChatNotFoundException, UserNotFoundException {
        if(chatServiceImplementation.doesChatExist(incomingMessage.getChatId())) {
            if (chatServiceImplementation.doesUserParticipateInChat(senderUsername, incomingMessage.getChatId())) {
                MessageEntity message = saveMessage(incomingMessage, senderUsername);
                MessageViewModel messageToSend = new MessageViewModel();
                modelMapper.map(message, messageToSend);
                messageToSend.setSenderUsername(senderUsername);
                if (message.getReplyingTo() != null) {
                    if (messageRepository.count() > incomingMessage.getMessageReplyId()) {
                        Optional<MessageEntity> messageReply = messageRepository
                                .findById(incomingMessage.getMessageReplyId());
                        if (messageReply.isPresent() && messageReply.get().getChat().getId().equals(incomingMessage.getChatId())) {
                            ReplyMessageViewModel reply = new ReplyMessageViewModel();
                            modelMapper.map(message.getReplyingTo(), reply);
                            messageToSend.setReplyTo(reply);
                        }
                    }
                }
                messageToSend.setChatId(incomingMessage.getChatId());
                messageToSend.setUnseenMessages(participantServiceImplementation
                        .returnParticipantUnseenMessagesByChatIdAndUsername(senderUsername, incomingMessage.getChatId()));
                templateServiceImplementation.sendMessageToUser(incomingMessage.getChatId(), messageToSend);
            } else throw new IllegalAccessException();
        } else throw new ChatNotFoundException();
    }

    @Override
    public List<MessageViewModel> loadPageableMessagesForChat(Long chatId, String usernameOfLoggedUser, int pageNum)
            throws IllegalAccessException, ChatNotFoundException, UserNotFoundException {
        if(chatServiceImplementation.doesChatExist(chatId)) {
            if (chatServiceImplementation.doesUserParticipateInChat(usernameOfLoggedUser, chatId)) {
                Pageable page = PageRequest.of(pageNum, 50);
                List<MessageEntity> messages = pageableMessageRepository.getByChat_IdOrderByCreateTimeDesc(chatId, page);
                Collections.reverse(messages);
                List<MessageViewModel> mappedMessages = new ArrayList<>();
                for (MessageEntity message :
                        messages) {
                    MessageViewModel mappedMessage = new MessageViewModel();
                    modelMapper.map(message, mappedMessage);
                    mappedMessage.setSenderNickname(message.getSender().getNickname());
                    mappedMessage.setSenderUsername(message.getSender().getUser().getUsername());
                    if (message.getReplyingTo() != null) {
                        ReplyMessageViewModel reply = new ReplyMessageViewModel();
                        modelMapper.map(message.getReplyingTo(), reply);
                        mappedMessage.setReplyTo(reply);
                    }
                    mappedMessages.add(mappedMessage);
                }
                participantServiceImplementation
                        .nullUnseenMessagesForParticipantByLoggedUserAndChatId(usernameOfLoggedUser, chatId);
                return mappedMessages;
            }
            throw new IllegalAccessException();
        }
        throw new ChatNotFoundException();
    }

    @Override
    public void deleteMessageById(Long messageId, String loggedUserUsername)
            throws NoSuchFieldException,
            IllegalAccessException {
        Optional<MessageEntity> message = messageRepository.findById(messageId);
        if(message.isPresent()){
            if(message.get().getSender().getUser().getUsername().equals(loggedUserUsername)){
                changeReplyMessageWhenDeletingMessage(message.get());
                messageRepository.delete(message.get());
            }
            else throw new IllegalAccessException();
        }
        else throw new NoSuchFieldException("Message not found.");
    }

    private MessageEntity saveMessage(MessageBindingModel incomingMessage, String senderUsername)
            throws ChatNotFoundException {
        ChatParticipantEntity sender = participantServiceImplementation
                .returnParticipantByChatIdAndUsername(senderUsername, incomingMessage.getChatId());
        ChatEntity chat = chatServiceImplementation.returnInnerChatById(incomingMessage.getChatId());
        MessageEntity newMessage = new MessageEntity();
        modelMapper.map(incomingMessage, newMessage);
        newMessage.setChat(chat);
        newMessage.setSender(sender);
        if (incomingMessage.getMessageReplyId() != null && incomingMessage.getMessageReplyId() != -1){
            Optional<MessageEntity> replyMessageOrNull = messageRepository
                    .findById(incomingMessage.getMessageReplyId());
            replyMessageOrNull.ifPresent(newMessage::setReplyingTo);
        }
        messageRepository.save(newMessage);
        participantServiceImplementation.switchUnseenMessagesForAllParticipantsOfAChat(incomingMessage.getChatId());
        List<ChatParticipantEntity> participantsWithClosedChat = chat
                .getParticipants()
                .stream()
                .filter(p -> p.getChatClosed() == true)
                .collect(Collectors.toList());
        if(participantsWithClosedChat.size() > 0){
            participantsWithClosedChat.forEach(p -> {
                participantServiceImplementation.openChatForParticipant(chat.getId(), p);
                templateServiceImplementation.sendChatToUser(p.getUser().getUsername(), chat.getId());
            });
        }
        return newMessage;
    }

    private void changeReplyMessageWhenDeletingMessage(MessageEntity message){
        List<MessageEntity> messagesToEdit = messageRepository.findAllByReplyingTo(message);
        if(messagesToEdit.size() > 0){
            for (MessageEntity messageToEdit :
                    messagesToEdit) {
                messageToEdit.setReplyingTo(null);
                messageToEdit.setReplyDeleted(true);
            }
        }
    }
}
