package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.ChatParticipantEntity;
import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.repositories.ParticipantRepository;
import com.messaging.messagingapp.services.ParticipantService;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParticipantServiceImplementation implements ParticipantService {
    private final ParticipantRepository participantRepository;
    private final UserServiceImplementation userServiceImplementation;

    public ParticipantServiceImplementation(ParticipantRepository participantRepository,
                                            UserServiceImplementation userServiceImplementation) {
        this.participantRepository = participantRepository;
        this.userServiceImplementation = userServiceImplementation;
    }

    @Override
    public ChatParticipantEntity returnParticipantById(Long id) {
        return null;
    }

    @Override
    public ChatParticipantEntity returnParticipantByChatIdAndUsername(String username, Long chatId)
            throws FileNotFoundException {
        Optional<ChatParticipantEntity> participantOrNull = participantRepository
                .findByChat_IdAndUser_Username(chatId, username);
        if(participantOrNull.isPresent())
            return participantOrNull.get();
        throw new FileNotFoundException("Participant not found.");
    }

    @Override
    public ChatParticipantEntity createAParticipant(String usernameOfUser, ChatEntity chat) {
        UserEntity user = userServiceImplementation.returnUserByUsername(usernameOfUser);
        ChatParticipantEntity newParticipant = new ChatParticipantEntity();
        newParticipant.setUser(user);
        newParticipant.setNickname(user.getPublicName());
        newParticipant.setChat(chat);
        participantRepository.save(newParticipant);
        return newParticipant;
    }

    @Override
    public List<ChatEntity> returnListOfChatsOfUser(String username) {
        List<ChatEntity> chats = participantRepository
                .getAllByUser_Username(username)
                .stream()
                .map(ChatParticipantEntity::getChat)
                .collect(Collectors.toList());
        return chats;
    }
}
