package com.messaging.messagingapp;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import com.messaging.messagingapp.data.repositories.ChatRepository;
import com.messaging.messagingapp.data.repositories.MessageRepository;
import com.messaging.messagingapp.data.repositories.ParticipantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class MessageControllerTests {
    final String loggedUserUsername = "admin";
    final String secondUserUsername = "test";
    final String thirdUserUsername = "test2";


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @AfterEach
    void outro(){
        this.messageRepository.deleteAll();
        this.participantRepository.deleteAll();
        this.chatRepository.deleteAll();
    }

    //Message sending tests

    @Test
    @WithMockUser("admin")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void sendValidMessageToValidChatAndLoad() throws Exception {
        createChat(this.loggedUserUsername);
        sendMessage(this.loggedUserUsername);
        mockMvc.perform(get("/message/load/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void sendValidMessageToForeignChat() throws Exception {
        createChat(this.loggedUserUsername);
        MultiValueMap<String, String> message = new LinkedMultiValueMap<>();
        message.add("textContent", "test");
        message.add("imageLink", "");
        message.add("chatId", "1");
        message.add("messageReplyId", "");
        mockMvc.perform(post("/message/send").params(message).with(user(this.thirdUserUsername)))
                .andExpect(status().is(403));
        Assertions.assertEquals(0, messageRepository.count());
        mockMvc.perform(get("/message/load/1").with(user(this.loggedUserUsername)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser("admin")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void sendValidMessageToNonExistentChat() throws Exception {
        MultiValueMap<String, String> message = new LinkedMultiValueMap<>();
        message.add("textContent", "test");
        message.add("imageLink", "");
        message.add("chatId", "210");
        message.add("messageReplyId", "");
        mockMvc.perform(post("/message/send").params(message).with(user(this.thirdUserUsername)))
                .andExpect(status().is(404));
        Assertions.assertEquals(0, messageRepository.count());
    }

    @Test
    @WithMockUser("admin")
    void sendMessageWithoutChatId() throws Exception {
        MultiValueMap<String, String> message = new LinkedMultiValueMap<>();
        message.add("textContent", "test");
        message.add("imageLink", "");
        message.add("messageReplyId", "");
        mockMvc.perform(post("/message/send").params(message).with(user(this.thirdUserUsername)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field cannot be empty"));
        Assertions.assertEquals(0, messageRepository.count());
    }

    @Test
    @WithMockUser("admin")
    void sendMessageWithEmptyChatId() throws Exception {
        MultiValueMap<String, String> message = new LinkedMultiValueMap<>();
        message.add("textContent", "test");
        message.add("imageLink", "");
        message.add("chatId", "");
        message.add("messageReplyId", "");
        mockMvc.perform(post("/message/send").params(message).with(user(this.thirdUserUsername)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field cannot be empty"));
        Assertions.assertEquals(0, messageRepository.count());
    }

    @Test
    @WithMockUser("admin")
    void sendMessageWithoutTextOrImage() throws Exception{
        MultiValueMap<String, String> message = new LinkedMultiValueMap<>();
        message.add("textContent", "");
        message.add("imageLink", "");
        message.add("chatId", "210");
        message.add("messageReplyId", "");
        mockMvc.perform(post("/message/send").params(message).with(user(this.thirdUserUsername)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field cannot be empty"));
        Assertions.assertEquals(0, messageRepository.count());
    }

    @Test
    @WithMockUser("admin")
    void sendMessageWithTextAndImageFullOfSpaces() throws Exception{
        MultiValueMap<String, String> message = new LinkedMultiValueMap<>();
        message.add("textContent", "                 ");
        message.add("imageLink", "                   ");
        message.add("chatId", "210");
        message.add("messageReplyId", "");
        mockMvc.perform(post("/message/send").params(message).with(user(this.thirdUserUsername)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field cannot be empty"));
        Assertions.assertEquals(0, messageRepository.count());
    }


    //Chat Loading tests

    @Test
    @WithMockUser("admin")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void loadValidChatMessages() throws Exception {
        createChat(this.loggedUserUsername);
        sendMessage(this.loggedUserUsername);
        mockMvc.perform(get("/message/load/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].senderUsername", is(this.loggedUserUsername)))
                .andExpect(jsonPath("$.[0].textContent", is("test")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void loadForeignChatMessages() throws Exception {
        createChat(this.loggedUserUsername);
        sendMessage(this.loggedUserUsername);
        mockMvc.perform(get("/message/load/1").with(user(this.thirdUserUsername)))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser("admin")
    void loadMessagesOfNonExistentChat() throws Exception {
        mockMvc.perform(get("/message/load/210"))
                .andExpect(status().is(404))
                .andExpect(content().string("Chat not found."));
    }

    //Delete message tests

    @Test
    @WithMockUser("admin")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void deleteValidMessageOfValidChat() throws Exception {
        createChat(this.loggedUserUsername);
        sendMessage(this.loggedUserUsername);
        mockMvc.perform(get("/message/load/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.[0].senderUsername", is(this.loggedUserUsername)));
        mockMvc.perform(delete("/message/1"))
                .andExpect(status().is(200));
        mockMvc.perform(get("/message/load/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
        Assertions.assertEquals(messageRepository.count(), 0);

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void deleteForeignMessageInAValidChat() throws Exception {
        createChat(this.loggedUserUsername);
        sendMessage(this.loggedUserUsername);
        mockMvc.perform(delete("/message/1").with(user(this.thirdUserUsername)))
                .andExpect(status().is(403));
        Assertions.assertEquals(messageRepository.count(), 1);
    }

    @Test
    @WithMockUser("admin")
    void deleteNonExistentMessage() throws Exception {
        Assertions.assertEquals(messageRepository.count(), 0);
        mockMvc.perform(delete("/message/210"))
                .andExpect(status().is(404))
                .andExpect(content().string("Message not found."));
    }


    private void createChat(String user) throws Exception {
        mockMvc.perform(post("/chat/create").with(user(user)).param("username", "test"))
                .andExpect(status().is(201));
    }
    private void sendMessage(String user) throws Exception{
        MultiValueMap<String, String> message = new LinkedMultiValueMap<>();
        message.add("textContent", "test");
        message.add("imageLink", "");
        message.add("chatId", "1");
        message.add("messageReplyId", "");
        mockMvc.perform(post("/message/send").params(message).with(user(user)))
                .andExpect(status().is(200));
        Assertions.assertEquals(1, messageRepository.count());
    }
}
