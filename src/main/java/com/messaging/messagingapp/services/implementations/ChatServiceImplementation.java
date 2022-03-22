package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.models.viewModel.ChatListViewModel;
import com.messaging.messagingapp.data.models.viewModel.ChatMessagesViewModel;
import com.messaging.messagingapp.data.models.viewModel.MessageViewModel;
import com.messaging.messagingapp.data.repositories.ChatRepository;
import com.messaging.messagingapp.services.ChatService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatServiceImplementation implements ChatService {
    private final ChatRepository chatRepository;
    private final UserServiceImplementation userServiceImplementation;
    private final ModelMapper modelMapper;

    public ChatServiceImplementation(ChatRepository chatRepository,
                                     UserServiceImplementation userServiceImplementation,
                                     ModelMapper modelMapper) {
        this.chatRepository = chatRepository;
        this.userServiceImplementation = userServiceImplementation;
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
    public ChatMessagesViewModel returnOuterChatById(Long chatId, String loggedUserUsername)
            throws NoSuchElementException {
        Optional<ChatEntity> unmappedChat = chatRepository.findById(chatId);
        if(unmappedChat.isPresent()){
            UserEntity loggedUser = userServiceImplementation.returnUserByUsername(loggedUserUsername);
            ChatMessagesViewModel chatToReturn = new ChatMessagesViewModel();
            modelMapper.map(unmappedChat.get(), chatToReturn);
            unmappedChat.get().getParticipants().forEach(p -> {
                if (p.getUser() != loggedUser){
                    chatToReturn.setParticipantNickname(p.getNickname());
                    chatToReturn.setParticipantProfilePicLink(p.getUser().getProfilePicLink());
                }
            });
            List<MessageViewModel> messageListToSend = new ArrayList<>();
            int messagesLength = unmappedChat.get().getMessages().size();
            for (int i = messagesLength - 51; i <= messagesLength - 1; i++){
                MessageViewModel messageToReturn = new MessageViewModel();
                modelMapper.map(unmappedChat.get().getMessages().get(i), messageToReturn);
                messageListToSend.add(messageToReturn);
            }
            chatToReturn.setLast50Messages(messageListToSend);
            return chatToReturn;
        }
        throw new NoSuchElementException("Chat not found.");
    }

    @Override
    public List<ChatListViewModel> loadChatListOfLoggedUser(String username) {
        UserEntity user = userServiceImplementation.returnUserByUsername(username);
        List<ChatListViewModel> listToReturn = new ArrayList<>();
        for (ChatEntity chat :
                user.getParticipants().stream().map(p -> p.getChat()).collect(Collectors.toList())) {
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
    public void createNewChat(String loggedUserUsername, Long otherUserId) {
        //TODO: Implement
    }
}
