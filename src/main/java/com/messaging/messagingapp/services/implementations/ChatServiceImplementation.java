package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.ChatParticipantEntity;
import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.models.viewModel.ChatListViewModel;
import com.messaging.messagingapp.data.repositories.ChatRepository;
import com.messaging.messagingapp.services.ChatService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatServiceImplementation implements ChatService {
    private final ChatRepository chatRepository;
    private final UserServiceImplementation userServiceImplementation;
    private final ParticipantServiceImplementation participantServiceImplementation;
    private final ModelMapper modelMapper;

    public ChatServiceImplementation(ChatRepository chatRepository,
                                     UserServiceImplementation userServiceImplementation,
                                     ParticipantServiceImplementation participantServiceImplementation,
                                     ModelMapper modelMapper) {
        this.chatRepository = chatRepository;
        this.userServiceImplementation = userServiceImplementation;
        this.participantServiceImplementation = participantServiceImplementation;
        this.modelMapper = modelMapper;
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
            });
            listToReturn.add(chatForList);
        }
        return listToReturn;
    }

    @Override
    public ChatEntity createNewChat(String loggedUserUsername, String otherUserUsername) {
        ChatEntity chat = new ChatEntity();
        chatRepository.save(chat);
        participantServiceImplementation.createAParticipant(loggedUserUsername, chat);
        participantServiceImplementation.createAParticipant(otherUserUsername, chat);
        return chat;
    }

    @Override
    public Boolean doesUserParticipateInChat(String username, Long chatId) {
        UserEntity user = userServiceImplementation.returnUserByUsername(username);
        List<Long> test = user.getParticipants().stream().map(p -> p.getChat().getId()).collect(Collectors.toList());
        if(test.contains(chatId)){
            return true;
        }
        return false;
    }
}
