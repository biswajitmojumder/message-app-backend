package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.ChatParticipantEntity;
import com.messaging.messagingapp.data.repositories.ParticipantRepository;
import com.messaging.messagingapp.services.ParticipantService;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.Optional;

@Service
public class ParticipantServiceImplementation implements ParticipantService {
    private final ParticipantRepository participantRepository;

    public ParticipantServiceImplementation(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
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
    public void changeParticipantNickname(Long id, String newNickname) {

    }
}
