package com.messaging.messagingapp;

import com.messaging.messagingapp.data.entities.RoleEntity;
import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.enums.RoleEnum;
import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;
import com.messaging.messagingapp.data.repositories.ChatRepository;
import com.messaging.messagingapp.data.repositories.RoleRepository;
import com.messaging.messagingapp.data.repositories.UserRepository;
import com.messaging.messagingapp.services.implementations.ChatServiceImplementation;
import com.messaging.messagingapp.services.implementations.UserServiceImplementation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements CommandLineRunner {
    private final UserServiceImplementation userServiceImplementation;
    private final ChatServiceImplementation chatServiceImplementation;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final PasswordEncoder passwordEncoder;
    private UserEntity firstUser;
    private UserEntity secondUser;

    public DataInit(UserServiceImplementation userServiceImplementation, ChatServiceImplementation chatServiceImplementation, RoleRepository roleRepository, UserRepository userRepository, ChatRepository chatRepository, PasswordEncoder passwordEncoder) {
        this.userServiceImplementation = userServiceImplementation;
        this.chatServiceImplementation = chatServiceImplementation;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        roleInit();
        userInit();
        chatInit();
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
    }
    private void chatInit(){
        if(chatRepository.count() == 0){
            chatServiceImplementation.createNewChat(secondUser.getUsername(), firstUser.getUsername());
        }
    }
}
