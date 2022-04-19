package com.messaging.messagingapp.web;

import com.messaging.messagingapp.data.models.viewModel.ChatListViewModel;
import com.messaging.messagingapp.services.implementations.ChatServiceImplementation;
import com.messaging.messagingapp.services.implementations.ParticipantServiceImplementation;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {
    private final ChatServiceImplementation chatServiceImplementation;
    private final ParticipantServiceImplementation participantServiceImplementation;

    public ChatController(
            ChatServiceImplementation chatServiceImplementation,
            ParticipantServiceImplementation participantServiceImplementation) {
        this.chatServiceImplementation = chatServiceImplementation;
        this.participantServiceImplementation = participantServiceImplementation;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ChatListViewModel>> loadChatsOfLoggedUser(Principal principal){
        List<ChatListViewModel> listOfChats = chatServiceImplementation.loadChatListOfLoggedUser(principal.getName());
        return ResponseEntity.ok(listOfChats);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createChatWithTwoParticipants(@RequestParam("username") String username, Principal principal){
        if(username.equals(principal.getName())){
            return ResponseEntity.status(409).body("You can't make a chat with yourself.");
        }
        else {
            try {
                chatServiceImplementation.createNewChat(principal.getName(), username);
            } catch (DuplicateKeyException e) {
                return ResponseEntity.status(409).body(e.getLocalizedMessage());
            }
            return ResponseEntity.status(201).build();
        }
    }

    @PatchMapping("/null-unseen-messages")
    public ResponseEntity<?> nullUnseenMessages(@RequestParam("chatId") Long chatId, Principal principal){
        try {
            participantServiceImplementation.nullUnseenMessagesForParticipantByLoggedUserAndChatId(principal.getName(), chatId);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body(e.getLocalizedMessage());
        }
        return ResponseEntity.ok().build();
    }
}
