package com.messaging.messagingapp;

import com.messaging.messagingapp.data.entities.RoleEntity;
import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.enums.RoleEnum;
import com.messaging.messagingapp.data.repositories.RoleRepository;
import com.messaging.messagingapp.data.repositories.UserRepository;
import com.messaging.messagingapp.services.implementations.UserServiceImplementation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("test")
public class TestDataInit implements CommandLineRunner {
    private final UserServiceImplementation userServiceImplementation;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private UserEntity firstUser;
    private UserEntity secondUser;
    private UserEntity thirdUser;

    public TestDataInit(UserServiceImplementation userServiceImplementation, UserRepository userRepository, RoleRepository roleRepository) {
        this.userServiceImplementation = userServiceImplementation;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0)
            rolesInit();
        if (userRepository.count() == 0)
            userInit();

    }

    private void rolesInit(){
        for (RoleEnum role :
                RoleEnum.values()) {
            if(roleRepository.findByRoleName(role).isEmpty()) {
                RoleEntity newRole = new RoleEntity();
                newRole.setRoleName(role);
                roleRepository.save(newRole);
            }
        }
    }
    private void userInit(){
        this.firstUser = new UserEntity();
        firstUser.setUsername("admin");
        firstUser.setPassword("test");
        firstUser.setPublicName("admin admin");
        firstUser.setEmail("test@admin.com");
        firstUser.setRoles(List.of(
                roleRepository.findByRoleName(RoleEnum.USER).get(),
                roleRepository.findByRoleName(RoleEnum.ADMIN).get()));

        this.secondUser = new UserEntity();
        secondUser.setUsername("test");
        secondUser.setPassword("test");
        secondUser.setPublicName("test test");
        secondUser.setEmail("test@test.com");
        secondUser.setRoles(List.of(
                roleRepository.findByRoleName(RoleEnum.USER).get()));

        this.thirdUser = new UserEntity();
        thirdUser.setUsername("test2");
        thirdUser.setPassword("test");
        thirdUser.setPublicName("test2 test2");
        thirdUser.setEmail("test2@test.com");
        thirdUser.setRoles(List.of(
                roleRepository.findByRoleName(RoleEnum.USER).get()));

        userRepository.saveAll(List.of(firstUser, secondUser, thirdUser));
    }
}
