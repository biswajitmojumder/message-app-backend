package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.ChatParticipantEntity;
import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.repositories.ParticipantRepository;
import com.messaging.messagingapp.exceptions.ChatNotFoundException;
import com.messaging.messagingapp.services.ParticipantService;
import org.springframework.stereotype.Service;

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
    public ChatParticipantEntity returnParticipantByChatIdAndUsername(String username, Long chatId)
            throws ChatNotFoundException {
        Optional<ChatParticipantEntity> participantOrNull = participantRepository
                .findByChat_IdAndUser_Username(chatId, username);
        if(participantOrNull.isPresent())
            return participantOrNull.get();
        throw new ChatNotFoundException("Chat not found.");
    }

    @Override
    public Boolean returnParticipantUnseenMessagesByChatIdAndUsername(String username, Long chatId) {
        return participantRepository.getUnseenMessagesByChatIdAndUsername(chatId, username);
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

    @Override
    public void nullUnseenMessagesForParticipantByLoggedUserAndChatId(String loggedUserUsername, Long chatId)
            throws ChatNotFoundException {
        ChatParticipantEntity participant = returnParticipantByChatIdAndUsername(loggedUserUsername, chatId);
        participant.setUnseenMessages(false);
        participantRepository.save(participant);
    }

    @Override
    public void switchUnseenMessagesForAllParticipantsOfAChat(Long chatId) throws ChatNotFoundException {
        List<ChatParticipantEntity> participantsOfChat = participantRepository.getAllByChat_Id(chatId);
        if(participantsOfChat.size() > 0) {
            for (ChatParticipantEntity participant :
                    participantsOfChat) {
                participant.setUnseenMessages(true);
                participantRepository.save(participant);
            }
        }
        else throw new ChatNotFoundException("This chat doesn't exist.");
    }

    @Override
    public void changeNicknameOfParticipantByChatIdAndUsername(String newNickname, Long chatId, String username)
            throws ChatNotFoundException {
        ChatParticipantEntity participant = returnParticipantByChatIdAndUsername(username, chatId);
        if (newNickname.isEmpty()){
            participant.setNickname(participant.getUser().getPublicName());
        }
        else participant.setNickname(newNickname);
        participantRepository.save(participant);
    }

    @Override
    public void closeChatForSingleUser(Long chatId, String loggedUserUsername) throws ChatNotFoundException {
        ChatParticipantEntity participant = returnParticipantByChatIdAndUsername(loggedUserUsername, chatId);
        participant.setChatClosed(true);
        participantRepository.save(participant);
    }

    @Override
    public void openChatForSingleUser(Long chatId, String loggedUserUsername) throws ChatNotFoundException {
        ChatParticipantEntity participant = returnParticipantByChatIdAndUsername(loggedUserUsername, chatId);
        participant.setChatClosed(false);
        participantRepository.save(participant);
    }

    @Override
    public void openChatForParticipant(Long chatId, ChatParticipantEntity participant) {
        participant.setChatClosed(false);
        participantRepository.save(participant);
    }
}
