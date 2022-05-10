package com.messaging.messagingapp.web;

import com.messaging.messagingapp.data.models.viewModel.ChatListViewModel;
import com.messaging.messagingapp.data.models.viewModel.ChatParticipantViewModel;
import com.messaging.messagingapp.exceptions.ChatNotFoundException;
import com.messaging.messagingapp.exceptions.UserNotFoundException;
import com.messaging.messagingapp.services.implementations.ChatServiceImplementation;
import com.messaging.messagingapp.services.implementations.ParticipantServiceImplementation;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> loadChatsOfLoggedUser(Principal principal){
        List<ChatListViewModel> listOfChats;
        try {
            listOfChats = chatServiceImplementation.loadChatListOfLoggedUser(principal.getName());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getLocalizedMessage());
        }
        return ResponseEntity.ok(listOfChats);
    }

    @GetMapping("/{chatId}/participant/all")
    public ResponseEntity<?> returnParticipantsListOfChat(@PathVariable Long chatId, Principal principal){
        if (chatId == null)
            return ResponseEntity.badRequest().body("Field cannot be empty");
        List<ChatParticipantViewModel> participants;
        try {
            participants = chatServiceImplementation.returnParticipantsOfChat(chatId, principal.getName());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).build();
        } catch (ChatNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getLocalizedMessage());
        }
        return ResponseEntity.ok(participants);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createChatWithTwoParticipants(@RequestParam("username") String username, Principal principal){
        if(username.trim().length() < 3)
            return ResponseEntity.badRequest().body("Field cannot be empty");
        else if(username.equals(principal.getName()))
            return ResponseEntity.status(409).body("You can't make a chat with yourself.");
        else {
            try {
                chatServiceImplementation.createNewChat(principal.getName(), username);
            } catch (DuplicateKeyException e) {
                return ResponseEntity.status(409).body(e.getLocalizedMessage());
            } catch (ChatNotFoundException | UserNotFoundException e) {
                return ResponseEntity.status(404).body(e.getLocalizedMessage());
            }
            return ResponseEntity.status(201).build();
        }
    }

    @PatchMapping("/null-unseen-messages")
    public ResponseEntity<?> nullUnseenMessages(@RequestParam("chatId") Long chatId, Principal principal){
        if(chatId == null)
            return ResponseEntity.badRequest().body("Field cannot be empty");
        try {
            participantServiceImplementation.nullUnseenMessagesForParticipantByLoggedUserAndChatId(principal.getName(), chatId);
        } catch (ChatNotFoundException e) {
            return ResponseEntity.status(404).body(e.getLocalizedMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{chatId}/participant/{username}")
    public ResponseEntity<?> changeParticipantNickname(
            @PathVariable("chatId") Long chatId,
            @PathVariable("username") String username,
            @RequestParam("nickname") String newNickname){
        if(chatId == null || username.trim().length() < 3)
            return ResponseEntity.badRequest().body("Field cannot be empty");
        try {
            participantServiceImplementation.changeNicknameOfParticipantByChatIdAndUsername(newNickname, chatId, username);
        } catch (ChatNotFoundException e) {
            return ResponseEntity.status(404).body(e.getLocalizedMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{chatId}/close")
    public ResponseEntity<?> closeChat(@PathVariable Long chatId, Principal principal){
        if(chatId == null)
            return ResponseEntity.badRequest().body("Field cannot be empty");
        try {
            participantServiceImplementation.closeChatForSingleUser(chatId, principal.getName());
        } catch (ChatNotFoundException e) {
            return ResponseEntity.status(404).body(e.getLocalizedMessage());
        }
        return ResponseEntity.ok().build();
    }
}
