package com.messaging.messagingapp.web;

import com.messaging.messagingapp.data.models.bindingModel.MessageBindingModel;
import com.messaging.messagingapp.data.models.viewModel.MessageViewModel;
import com.messaging.messagingapp.exceptions.ChatNotFoundException;
import com.messaging.messagingapp.exceptions.UserNotFoundException;
import com.messaging.messagingapp.services.implementations.MessageServiceImplementation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
        if(
                message.getChatId() == null ||
                message.getTextContent().trim().isEmpty() && message.getImageLink().trim().isEmpty()){
            return ResponseEntity.badRequest().body("Field cannot be empty");
        }
        try {
            messageServiceImplementation.sendMessage(message, principal.getName());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).build();
        } catch (ChatNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/load/{chatId}")
    public ResponseEntity<?> loadChatMessages(
            @PathVariable Long chatId,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNum,
            Principal principal){
        if (chatId == null)
            return ResponseEntity.badRequest().body("Field cannot be empty");
        List<MessageViewModel> messages;
        try {
            messages = messageServiceImplementation.loadPageableMessagesForChat(chatId, principal.getName(), pageNum);
        } catch (IllegalAccessException | ChatNotFoundException e) {
            return ResponseEntity.status(403).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getLocalizedMessage());
        }
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("{messageId}")
    public ResponseEntity<?> deleteChatMessage(@PathVariable Long messageId, Principal principal){
        if(messageId == null)
            return ResponseEntity.badRequest().body("Field cannot be empty");
        try {
            messageServiceImplementation.deleteMessageById(messageId, principal.getName());
        } catch (NoSuchFieldException e) {
            return ResponseEntity.status(404).body(e.getLocalizedMessage());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok().build();
    }
}
