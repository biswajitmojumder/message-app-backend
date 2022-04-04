package com.messaging.messagingapp.web;

import com.messaging.messagingapp.data.models.bindingModel.MessageBindingModel;
import com.messaging.messagingapp.data.models.viewModel.MessageViewModel;
import com.messaging.messagingapp.services.implementations.MessageServiceImplementation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/message")
public class MessageController {
    private final MessageServiceImplementation messageServiceImplementation;

    public MessageController(MessageServiceImplementation messageServiceImplementation) {
        this.messageServiceImplementation = messageServiceImplementation;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(MessageBindingModel message, Principal principal) {
        try {
            messageServiceImplementation.sendMessage(message, principal.getName());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).build();
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/load/{chatId}")
    public ResponseEntity<?> loadChatMessages(@PathVariable Long chatId, Principal principal){
        List<MessageViewModel> messages;
        try {
            messages = messageServiceImplementation.loadMessagesForChat(chatId, principal.getName());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(messages);
    }
}
