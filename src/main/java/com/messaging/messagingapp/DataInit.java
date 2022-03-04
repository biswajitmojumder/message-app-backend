package com.messaging.messagingapp;

import com.messaging.messagingapp.data.entities.RoleEntity;
import com.messaging.messagingapp.data.enums.RoleEnum;
import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;
import com.messaging.messagingapp.data.repositories.RoleRepository;
import com.messaging.messagingapp.data.repositories.UserRepository;
import com.messaging.messagingapp.services.implementations.UserServiceImplementation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements CommandLineRunner {
    private final UserServiceImplementation userServiceImplementation;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInit(UserServiceImplementation userServiceImplementation, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userServiceImplementation = userServiceImplementation;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        roleInit();
        userInit();
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
            userServiceImplementation.registerUser(
                    new RegisterUserBindingModel(
                            "admin",
                            passwordEncoder.encode("test"),
                            "test@admin.bg",
                            "admin adminov"));
        }
    }
}
