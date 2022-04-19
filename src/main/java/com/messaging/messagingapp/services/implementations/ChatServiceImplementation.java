package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.ChatParticipantEntity;
import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.models.viewModel.ChatListViewModel;
import com.messaging.messagingapp.data.repositories.ChatRepository;
import com.messaging.messagingapp.data.repositories.ParticipantRepository;
import com.messaging.messagingapp.services.ChatService;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatServiceImplementation implements ChatService {
    private final ChatRepository chatRepository;
    private final ParticipantRepository participantRepository;
    private final UserServiceImplementation userServiceImplementation;
    private final ParticipantServiceImplementation participantServiceImplementation;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatServiceImplementation(ChatRepository chatRepository,
                                     ParticipantRepository participantRepository,
                                     UserServiceImplementation userServiceImplementation,
                                     ParticipantServiceImplementation participantServiceImplementation,
                                     ModelMapper modelMapper,
                                     SimpMessagingTemplate messagingTemplate) {
        this.chatRepository = chatRepository;
        this.participantRepository = participantRepository;
        this.userServiceImplementation = userServiceImplementation;
        this.participantServiceImplementation = participantServiceImplementation;
        this.modelMapper = modelMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public ChatEntity returnInnerChatById(Long chatId) throws FileNotFoundException {
        Optional<ChatEntity> chatOrNull = chatRepository.findById(chatId);
        if(chatOrNull.isPresent())
            return chatOrNull.get();
        else throw new FileNotFoundException("Chat not found.");
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
        return listToReturn;
    }

    @Override
    public ChatEntity createNewChat(String loggedUserUsername, String otherUserUsername) throws DuplicateKeyException{
        if(!doesLoggedUserHaveAChatWithOtherUser(loggedUserUsername, otherUserUsername)) {
            ChatEntity chat = new ChatEntity();
            chatRepository.save(chat);
            participantServiceImplementation.createAParticipant(loggedUserUsername, chat);
            participantServiceImplementation.createAParticipant(otherUserUsername, chat);
            messagingTemplate.convertAndSend("/queue/chat-list/" + loggedUserUsername, chat.getId());
            messagingTemplate.convertAndSend("/queue/chat-list/" + otherUserUsername, chat.getId());
            return chat;
        }
        else throw new DuplicateKeyException("You already have a chat with this user!");
    }

    @Override
    @Transactional
    public Boolean doesUserParticipateInChat(String username, Long chatId) {
        UserEntity user = userServiceImplementation.returnUserByUsername(username);
        List<Long> test = user.getParticipants().stream().map(p -> p.getChat().getId()).collect(Collectors.toList());
        if(test.contains(chatId)){
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

    @Override
    public void increaseUnseenMessagesForAllParticipantsOfAChat(Long chatId) {
        List<ChatParticipantEntity> participantsOfChat = chatRepository.returnParticipantsOfChat(chatId);
        for (ChatParticipantEntity participant :
                participantsOfChat) {
            participant.setUnseenMessages(true);
            participantRepository.save(participant);
        }
    }
}
