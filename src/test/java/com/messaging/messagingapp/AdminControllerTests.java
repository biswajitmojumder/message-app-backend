package com.messaging.messagingapp;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.repositories.RoleRepository;
import com.messaging.messagingapp.data.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTests {
    private static MultiValueMap<String, String> registerUser = new LinkedMultiValueMap<>();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeAll
    static void init(){
        registerUser.add("username", "test");
        registerUser.add("password", "test");
        registerUser.add("confirmPassword", "test");
        registerUser.add("email", "test@test.bg");
        registerUser.add("publicName", "test test");
    }
}
