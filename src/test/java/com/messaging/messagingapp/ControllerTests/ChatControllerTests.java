package com.messaging.messagingapp.ControllerTests;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.messaging.messagingapp.data.entities.ChatEntity;
import com.messaging.messagingapp.data.repositories.ChatRepository;
import com.messaging.messagingapp.data.repositories.MessageRepository;
import com.messaging.messagingapp.data.repositories.ParticipantRepository;
import com.messaging.messagingapp.data.repositories.UserRepository;
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

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class ChatControllerTests {
    final String loggedUserUsername = "admin";
    final String secondUserUsername = "test";
    final String thirdUserUsername = "test2";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void outro(){
        messageRepository.deleteAll();
        participantRepository.deleteAll();
        chatRepository.deleteAll();
    }

    //chat creation tests

    @Test
    @WithMockUser("admin")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void createValidChat() throws Exception {
        checkIfChatRepoIsEmpty();
        checkIfParticipantRepoIsEmpty();
        createChat();
        Assertions.assertEquals(1, chatRepository.count());
        Assertions.assertEquals(2, participantRepository.count());

        Long loggedUserId = userRepository.findByUsername(loggedUserUsername).get().getId();
        Long otherUserId = userRepository.findByUsername(secondUserUsername).get().getId();

        for (ChatEntity chat :
                chatRepository.findAll()) {
            System.out.println(chat.getId());
        }

        Assertions.assertTrue(
                chatRepository
                        .returnParticipantsOfChat(1L)
                        .get()
                        .stream()
                        .map(p -> p.getUser().getId())
                        .anyMatch(p -> p.equals(loggedUserId)));
        Assertions.assertTrue(
                chatRepository
                        .returnParticipantsOfChat(1L)
                        .get()
                        .stream()
                        .map(p -> p.getUser().getId())
                        .anyMatch(p -> p.equals(otherUserId)));
    }

    @Test
    @WithMockUser("admin")
    void createSecondChatWithSameUser() throws Exception {
        checkIfChatRepoIsEmpty();
        checkIfParticipantRepoIsEmpty();
        createChat();
        mockMvc.perform(post("/chat/create").param("username", "test"))
                .andExpect(status().is(409))
                .andExpect(content().string("You already have a chat with this user!"));
        Assertions.assertEquals(1, chatRepository.count());
        Assertions.assertEquals(2, participantRepository.count());
    }

    @Test
    @WithMockUser("admin")
    void createChatWithLoggedUser() throws Exception {
        checkIfChatRepoIsEmpty();
        checkIfParticipantRepoIsEmpty();
        mockMvc.perform(post("/chat/create").param("username", "admin"))
                .andExpect(status().is(409))
                .andExpect(content().string("You can't make a chat with yourself."));
        checkIfChatRepoIsEmpty();
        checkIfParticipantRepoIsEmpty();
    }

    @Test
    @WithMockUser("admin")
    void createChatWithoutUsernameParameter() throws Exception {
        checkIfChatRepoIsEmpty();
        checkIfParticipantRepoIsEmpty();
        mockMvc.perform(post("/chat/create"))
                .andExpect(status().is(400))
                .andExpect(content().string("Field cannot be empty"));
        checkIfChatRepoIsEmpty();
        checkIfParticipantRepoIsEmpty();
    }

    @Test
    @WithMockUser("admin")
    void createChatWithEmptyUsernameParameter() throws Exception {
        checkIfChatRepoIsEmpty();
        checkIfParticipantRepoIsEmpty();
        mockMvc.perform(post("/chat/create").param("username", ""))
                .andExpect(status().is(400))
                .andExpect(content().string("Field cannot be empty"));
        checkIfChatRepoIsEmpty();
        checkIfParticipantRepoIsEmpty();
    }

    @Test
    @WithMockUser("admin")
    void createChatWithNonExistentUser() throws Exception {
        checkIfChatRepoIsEmpty();
        checkIfParticipantRepoIsEmpty();
        mockMvc.perform(post("/chat/create").param("username", "iDontExist"))
                .andExpect(status().is(404))
                .andExpect(content().string("User not found"));
        checkIfChatRepoIsEmpty();
        checkIfParticipantRepoIsEmpty();
    }

    //chat loading test

    @Test
    @WithMockUser("admin")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void loadAllChatsOfLoggedUser() throws Exception{
        mockMvc.perform(get("/chat/all"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
        createChat();
        mockMvc.perform(get("/chat/all"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].chatParticipantName",
                        is(userRepository.findByUsername(secondUserUsername).get().getPublicName())));
        mockMvc.perform(post("/chat/create").param("username", this.thirdUserUsername))
                .andExpect(status().is(201));
        mockMvc.perform(get("/chat/all"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].chatParticipantName",
                        is(userRepository.findByUsername(secondUserUsername).get().getPublicName())))
                .andExpect(jsonPath("$.[1].id", is(2)))
                .andExpect(jsonPath("$.[1].chatParticipantName",
                        is(userRepository.findByUsername(thirdUserUsername).get().getPublicName())));
    }

    @Test
    @WithMockUser("admin")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void closeValidChat() throws Exception{
        createChat();
        mockMvc.perform(get("/chat/all"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(patch("/chat/1/close"))
                .andExpect(status().is(200));

        mockMvc.perform(get("/chat/all"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser("admin")
    void closeNonExistentChat() throws Exception{
        mockMvc.perform(patch("/chat/210/close"))
                .andExpect(status().is(404))
                .andExpect(content().string("Chat not found."));
    }

    //chat participants loading tests

    @Test
    @WithMockUser("admin")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void loadParticipantsOfValidChat() throws Exception{
        createChat();
        mockMvc.perform(get("/chat/1/participant/all"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].username", is(loggedUserUsername)))
                .andExpect(jsonPath("$.[0].publicName",
                        is(userRepository.findByUsername(loggedUserUsername).get().getPublicName())))
                .andExpect(jsonPath("$.[0].nickname",
                        is(userRepository.findByUsername(loggedUserUsername).get().getPublicName())))
                .andExpect(jsonPath("$.[1].username", is(secondUserUsername)))
                .andExpect(jsonPath("$.[1].publicName",
                        is(userRepository.findByUsername(secondUserUsername).get().getPublicName())))
                .andExpect(jsonPath("$.[1].nickname",
                        is(userRepository.findByUsername(secondUserUsername).get().getPublicName())));
    }

    @Test
    @WithMockUser("admin")
    void loadParticipantsOfNonExistentChat() throws Exception{
        mockMvc.perform(get("/chat/210/participant/all"))
                .andExpect(status().is(404))
                .andExpect(content().string("Chat not found."));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void loadParticipantsOfForeignChat() throws Exception{
        mockMvc.perform(post("/chat/create").with(user(this.thirdUserUsername)).param("username", "test"))
                .andExpect(status().is(201));
        mockMvc.perform(get("/chat/1/participant/all").with(user(this.loggedUserUsername)))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser("admin")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void changeParticipantNickname() throws Exception{
        createChat();
        mockMvc.perform(get("/chat/1/participant/all"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].nickname", is("admin admin")))
                .andExpect(jsonPath("$.[1].nickname", is("test test")));

        String newNickname = "newNickname";
        mockMvc.perform(patch("/chat/1/participant/" + this.loggedUserUsername).param("nickname", newNickname))
                .andExpect(status().is(200));

        mockMvc.perform(get("/chat/1/participant/all"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].nickname", is(newNickname)))
                .andExpect(jsonPath("$.[1].nickname", is("test test")));
    }

    @Test
    @WithMockUser("admin")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void changeParticipantNicknameWithEmptyNicknameParam() throws Exception{
        createChat();
        mockMvc.perform(get("/chat/1/participant/all"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].nickname", is("admin admin")))
                .andExpect(jsonPath("$.[1].nickname", is("test test")));

        mockMvc.perform(patch("/chat/1/participant/" + this.loggedUserUsername).param("nickname", ""))
                .andExpect(status().is(200));

        mockMvc.perform(get("/chat/1/participant/all"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].nickname", is("admin admin")))
                .andExpect(jsonPath("$.[1].nickname", is("test test")));
    }

    @Test
    @WithMockUser("admin")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void changeParticipantNameWithoutParameter() throws Exception{
        createChat();
        mockMvc.perform(patch("/chat/1/participant/" + this.loggedUserUsername))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Missing new nickname parameter"));
    }

    @Test
    @WithMockUser("admin")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void changeParticipantNameWithUsernameThatIsTooShort() throws Exception{
        createChat();
        mockMvc.perform(patch("/chat/1/participant/sh").param("nickname", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username cannot be less than 3 letters long"));
    }

    @Test
    @WithMockUser("admin")
    void changeParticipantNameOfNonExistentChat() throws Exception{
        mockMvc.perform(patch("/chat/210/participant/" + this.loggedUserUsername)
                        .param("nickname", "test"))
                .andExpect(status().is(404))
                .andExpect(content().string("Chat not found."));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void nullUnseenMessages() throws Exception{
        createChat();
        MultiValueMap<String, String> message = new LinkedMultiValueMap<>();
        message.add("textContent", "test");
        message.add("imageLink", "");
        message.add("chatId", "1");
        message.add("messageReplyId", "");
        mockMvc.perform(post("/message/send").params(message).with(user(this.secondUserUsername)))
                .andExpect(status().is(200));
        mockMvc.perform(get("/chat/all").with(user(this.loggedUserUsername)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].unseenMessages", is(true)));
        mockMvc.perform(patch("/chat/1/null-unseen-messages").with(user(this.loggedUserUsername)))
                .andExpect(status().is(200));
        mockMvc.perform(get("/chat/all").with(user(this.loggedUserUsername)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].unseenMessages", is(false)));
    }

    @Test
    @WithMockUser("admin")
    void nullUnseenMessagesForANonExistentChat() throws Exception{
        mockMvc.perform(patch("/chat/210/null-unseen-messages"))
                .andExpect(status().is(404))
                .andExpect(content().string("Chat not found."));
    }

    private void createChat() throws Exception {
        mockMvc.perform(post("/chat/create").with(user("admin")).param("username", "test"))
                .andExpect(status().is(201));
    }
    private void checkIfChatRepoIsEmpty(){
        Assertions.assertEquals(0, chatRepository.count());
    }
    private void checkIfParticipantRepoIsEmpty(){
        Assertions.assertEquals(0, participantRepository.count());
    }
}
