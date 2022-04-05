package com.messaging.messagingapp;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.entities.RoleEntity;
import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.enums.RoleEnum;
import com.messaging.messagingapp.data.models.bindingModel.MessageBindingModel;
import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;
import com.messaging.messagingapp.data.repositories.ChatRepository;
import com.messaging.messagingapp.data.repositories.MessageRepository;
import com.messaging.messagingapp.data.repositories.RoleRepository;
import com.messaging.messagingapp.data.repositories.UserRepository;
import com.messaging.messagingapp.services.implementations.ChatServiceImplementation;
import com.messaging.messagingapp.services.implementations.MessageServiceImplementation;
import com.messaging.messagingapp.services.implementations.UserServiceImplementation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Component
public class DataInit implements CommandLineRunner {
    private final UserServiceImplementation userServiceImplementation;
    private final ChatServiceImplementation chatServiceImplementation;
    private final MessageServiceImplementation messageServiceImplementation;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private UserEntity firstUser;
    private UserEntity secondUser;
    private ChatEntity chat;

    public DataInit(UserServiceImplementation userServiceImplementation,
                    ChatServiceImplementation chatServiceImplementation,
                    MessageServiceImplementation messageServiceImplementation,
                    RoleRepository roleRepository,
                    UserRepository userRepository,
                    ChatRepository chatRepository, MessageRepository messageRepository) {
        this.userServiceImplementation = userServiceImplementation;
        this.chatServiceImplementation = chatServiceImplementation;
        this.messageServiceImplementation = messageServiceImplementation;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        roleInit();
        userInit();
        chatInit();
        messageInit();
    }

    private void roleInit(){
        if(roleRepository.count() < RoleEnum.values().length){
            for (RoleEnum role :
                    RoleEnum.values()) {
                if(roleRepository.findByRoleName(role).isEmpty()) {
                    RoleEntity newRole = new RoleEntity();
                    newRole.setRoleName(role);
                    roleRepository.save(newRole);
                }
            }
        }
    }
    private void userInit(){
        if (userRepository.count() == 0){
            this.firstUser = userServiceImplementation.registerUser(
                    new RegisterUserBindingModel(
                            "admin",
                            "test",
                            "test@admin.bg",
                            "admin adminov"));
            this.secondUser = userServiceImplementation.registerUser(
                    new RegisterUserBindingModel(
                            "test",
                            "test",
                            "test@test.bg",
                            "test testov"
                    )
            );
        }
        else {
            this.firstUser = userRepository.findByUsername("admin").get();
            this.secondUser = userRepository.findByUsername("test").get();
        }
    }
    private void chatInit(){
        if(chatRepository.count() == 0){
            chat = chatServiceImplementation.createNewChat(secondUser.getUsername(), firstUser.getUsername());
        }
        else
            chat = chatRepository.getById(1L);
    }
    private void messageInit() throws FileNotFoundException, IllegalAccessException {
        if(messageRepository.count() == 0){
            for (int i = 0; i <= 69; i++){
                MessageBindingModel firstMessage = new MessageBindingModel();
                firstMessage.setChatId(chat.getId());
                firstMessage.setTextContent("test message " + i);
                messageServiceImplementation.sendMessage(firstMessage, this.firstUser.getUsername());
            }
        }
    }
}
