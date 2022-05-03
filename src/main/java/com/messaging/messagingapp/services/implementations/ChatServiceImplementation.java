package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.ChatParticipantEntity;
import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.models.viewModel.ChatListViewModel;
import com.messaging.messagingapp.data.models.viewModel.ChatParticipantViewModel;
import com.messaging.messagingapp.data.repositories.ChatRepository;
import com.messaging.messagingapp.exceptions.ChatNotFoundException;
import com.messaging.messagingapp.services.ChatService;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatServiceImplementation implements ChatService {
    private final ChatRepository chatRepository;
    private final UserServiceImplementation userServiceImplementation;
    private final ParticipantServiceImplementation participantServiceImplementation;
    private final MessagingTemplateServiceImplementation templateServiceImplementation;
    private final ModelMapper modelMapper;

    public ChatServiceImplementation(ChatRepository chatRepository,
                                     UserServiceImplementation userServiceImplementation,
                                     ParticipantServiceImplementation participantServiceImplementation,
                                     MessagingTemplateServiceImplementation templateServiceImplementation,
                                     ModelMapper modelMapper) {
        this.chatRepository = chatRepository;
        this.userServiceImplementation = userServiceImplementation;
        this.participantServiceImplementation = participantServiceImplementation;
        this.templateServiceImplementation = templateServiceImplementation;
        this.modelMapper = modelMapper;
    }

    @Override
    public ChatEntity returnInnerChatById(Long chatId) throws ChatNotFoundException {
        Optional<ChatEntity> chatOrNull = chatRepository.findById(chatId);
        if(chatOrNull.isPresent())
            return chatOrNull.get();
        else throw new ChatNotFoundException("Chat not found.");
    }


    @Override
    public List<ChatListViewModel> loadChatListOfLoggedUser(String username) {
        UserEntity user = userServiceImplementation.returnUserByUsername(username);
        List<ChatListViewModel> listToReturn = new ArrayList<>();
        List<ChatEntity> chats = user
                .getParticipants()
                .stream()
                .map(ChatParticipantEntity::getChat)
                .collect(Collectors.toList());
        for (ChatEntity chat :
                chats) {
            if(!chat.getParticipants()
                    .stream()
                    .filter(p -> p.getUser().getUsername().equals(username))
                    .map(ChatParticipantEntity::getChatClosed)
                    .findFirst()
                    .get()){
                ChatListViewModel chatForList = new ChatListViewModel();
                modelMapper.map(chat, chatForList);
                chat.getParticipants().forEach(p -> {
                    if(p.getUser() != user){
                        chatForList.setChatParticipantName(p.getNickname());
                        chatForList.setChatParticipantImageLink(p.getUser().getProfilePicLink());
                    }
                    else {
                        chatForList.setUnseenMessages(p.getUnseenMessages());
                    }
                });
                listToReturn.add(chatForList);
            }
        }
        return listToReturn;
    }

    @Override
    public List<ChatParticipantViewModel> returnParticipantsOfChat(Long chatId, String loggedUserUsername)
            throws IllegalAccessException,
            ChatNotFoundException {
        if(doesUserParticipateInChat(loggedUserUsername, chatId)){
            List<ChatParticipantViewModel> participantsToReturn = new ArrayList<>();
            Optional<List<ChatParticipantEntity>> participantsOrNull = chatRepository.returnParticipantsOfChat(chatId);
            if(participantsOrNull.isPresent()) {
                for (ChatParticipantEntity participant :
                        participantsOrNull.get()) {
                    ChatParticipantViewModel mappedParticipant = new ChatParticipantViewModel();
                    modelMapper.map(participant, mappedParticipant);
                    mappedParticipant.setUsername(participant.getUser().getUsername());
                    mappedParticipant.setPublicName(participant.getUser().getPublicName());
                    mappedParticipant.setProfilePicLink(participant.getUser().getProfilePicLink());
                    participantsToReturn.add(mappedParticipant);
                }
                return participantsToReturn;
            }
            throw new ChatNotFoundException("This chat doesn't exist.");
        }
        throw new IllegalAccessException();
    }

    @Override
    public ChatEntity createNewChat(String loggedUserUsername, String otherUserUsername) throws DuplicateKeyException, ChatNotFoundException {
        if(!doesLoggedUserHaveAChatWithOtherUser(loggedUserUsername, otherUserUsername)) {
            ChatEntity chat = new ChatEntity();
            chatRepository.save(chat);
            participantServiceImplementation.createAParticipant(loggedUserUsername, chat);
            participantServiceImplementation.createAParticipant(otherUserUsername, chat);
            templateServiceImplementation.sendChatToUser(loggedUserUsername, chat.getId());
            templateServiceImplementation.sendChatToUser(otherUserUsername, chat.getId());
            return chat;
        }
        else {
            Long chatId = 0L;
            for (ChatEntity chat :
                    participantServiceImplementation.returnListOfChatsOfUser(loggedUserUsername)) {
                for (ChatParticipantEntity participant:
                        chat.getParticipants()) {
                    if (participant.getUser().getUsername().equals(otherUserUsername)){
                        chatId = participant.getChat().getId();
                    }
                }
            }
            if(isChatClosedForLoggedUser(loggedUserUsername, chatId)){
                participantServiceImplementation.openChatForSingleUser(chatId, loggedUserUsername);
                templateServiceImplementation.sendChatToUser(loggedUserUsername, chatId);
                return returnInnerChatById(chatId);
            }
            else throw new DuplicateKeyException("You already have a chat with this user!");
        }
    }

    @Override
    @Transactional
    public Boolean doesUserParticipateInChat(String username, Long chatId) {
        UserEntity user = userServiceImplementation.returnUserByUsername(username);
        List<Long> chatIds = user.getParticipants().stream().map(p -> p.getChat().getId()).collect(Collectors.toList());
        if(chatIds.contains(chatId)){
            return true;
        }
        return false;
    }

    @Override
    public Boolean doesLoggedUserHaveAChatWithOtherUser(String loggedUserUsername, String otherUserUsername) {
        for (ChatEntity chat :
                participantServiceImplementation.returnListOfChatsOfUser(loggedUserUsername)) {
            for (ChatParticipantEntity participant:
                 chat.getParticipants()) {
                if (participant.getUser().getUsername().equals(otherUserUsername)){
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean isChatClosedForLoggedUser(String loggedUserUsername, Long chatId) throws ChatNotFoundException {
        ChatParticipantEntity chat = returnInnerChatById(chatId)
                .getParticipants()
                .stream()
                .filter(p -> p.getUser().getUsername().equals(loggedUserUsername))
                .findFirst()
                .get();
        return chat.getChatClosed();
    }
}
