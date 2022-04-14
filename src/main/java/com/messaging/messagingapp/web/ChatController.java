package com.messaging.messagingapp.web;

import com.messaging.messagingapp.data.models.viewModel.ChatListViewModel;
import com.messaging.messagingapp.services.implementations.ChatServiceImplementation;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {
    private final ChatServiceImplementation chatServiceImplementation;

    public ChatController(ChatServiceImplementation chatServiceImplementation) {
        this.chatServiceImplementation = chatServiceImplementation;
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
}
