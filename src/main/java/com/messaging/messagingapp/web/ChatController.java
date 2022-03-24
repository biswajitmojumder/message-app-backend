package com.messaging.messagingapp.web;

import com.messaging.messagingapp.data.models.viewModel.ChatListViewModel;
import com.messaging.messagingapp.data.models.viewModel.ChatMessagesViewModel;
import com.messaging.messagingapp.services.implementations.ChatServiceImplementation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class ChatController {
    private final ChatServiceImplementation chatServiceImplementation;

    public ChatController(ChatServiceImplementation chatServiceImplementation) {
        this.chatServiceImplementation = chatServiceImplementation;
    }

    @GetMapping("/chat/all")
    public ResponseEntity<List<ChatListViewModel>> loadChatsOfLoggedUser(Principal principal){
        List<ChatListViewModel> listOfChats = chatServiceImplementation.loadChatListOfLoggedUser(principal.getName());
        return ResponseEntity.ok(listOfChats);
    }

    @PostMapping("/chat/create")
    public ResponseEntity<?> createChatWithTwoParticipants(String username, Principal principal){
        chatServiceImplementation.createNewChat(principal.getName(), username);
        return ResponseEntity.status(201).build();
    }
    @GetMapping("/chat/{id}")
    public ResponseEntity<ChatMessagesViewModel> returnChatById(@PathVariable Long id, Principal principal){
        ChatMessagesViewModel chat = chatServiceImplementation.returnOuterChatById(id, principal.getName());
        return ResponseEntity.ok(chat);
    }
}
