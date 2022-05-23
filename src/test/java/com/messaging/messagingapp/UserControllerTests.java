package com.messaging.messagingapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    //is-logged-in tests

    @Test
    void isUserLoggedWhenNoUserIsLogged() throws Exception {
        mockMvc.perform(get("/is-logged-in"))
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser("admin")
    void isUserLoggedWithLoggedUser() throws Exception {
        mockMvc.perform(get("/is-logged-in"))
                .andExpect(status().is(200));
    }

    //small user info test

    @Test
    void smallInfoOfLoggedUser() throws Exception {
        mockMvc.perform(get("/user-info/small").with(user("admin")))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.publicName", is("admin admin")))
                .andExpect(jsonPath("$.username", is("admin")))
                .andExpect(jsonPath("$.admin", is(true)));

        mockMvc.perform(get("/user-info/small").with(user("test")))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.publicName", is("test test")))
                .andExpect(jsonPath("$.username", is("test")))
                .andExpect(jsonPath("$.admin", is(false)));
    }

    //search user tests

    @Test
    @WithMockUser("admin")
    void searchForAValidUser() throws Exception {
        mockMvc.perform(get("/search").param("username", "test"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/search").param("username", "test2"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].username", is("test2")));
    }

    @Test
    @WithMockUser("admin")
    void searchForANonExistentUser() throws Exception {
        mockMvc.perform(get("/search").param("username", "test210"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser("admin")
    void searchWithNoUsernameParam() throws Exception {
        mockMvc.perform(get("/search"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field cannot be empty"));
    }

    @Test
    @WithMockUser("admin")
    void searchWithParamFullOfSpaces() throws Exception {
        mockMvc.perform(get("/search").param("username", "          "))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field cannot be empty"));
    }

    //Change user pfp tests

    @Test
    @WithMockUser("admin")
    void changeProfilePictureLink() throws Exception {
        mockMvc.perform(get("/user-info/small"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.profilePicLink", is(nullValue())));

        mockMvc.perform(patch("/change/profile-picture-link")
                        .param("profilePictureLink", "testPPLink"))
                .andExpect(status().is(200));

        mockMvc.perform(get("/user-info/small"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.profilePicLink", is("testPPLink")));
    }

    @Test
    @WithMockUser("admin")
    void changePPLinkWithNoParam() throws Exception {
        mockMvc.perform(patch("/change/profile-picture-link"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field cannot be empty"));
    }

    @Test
    @WithMockUser("admin")
    void changePPLinkWithParamFullOfSpaces() throws Exception {
        mockMvc.perform(patch("/change/profile-picture-link").param("profilePictureLink", "    "))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field cannot be empty"));
    }

    //Change public name tests

    @Test
    @WithMockUser("admin")
    void changePublicName() throws Exception {
        mockMvc.perform(get("/user-info/small"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.publicName", is("admin admin")));

        mockMvc.perform(patch("/change/public-name").param("publicName", "new name"))
                .andExpect(status().is(200));

        mockMvc.perform(get("/user-info/small"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.publicName", is("new name")));

        mockMvc.perform(patch("/change/public-name").param("publicName", "admin admin"))
                .andExpect(status().is(200));

        mockMvc.perform(get("/user-info/small"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.publicName", is("admin admin")));
    }

    @Test
    @WithMockUser("admin")
    void changePublicNameWithNoParam() throws Exception {
        mockMvc.perform(get("/user-info/small"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.publicName", is("admin admin")));

        mockMvc.perform(patch("/change/public-name"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field cannot be empty"));

        mockMvc.perform(get("/user-info/small"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.publicName", is("admin admin")));
    }

    @Test
    @WithMockUser("admin")
    void changePublicNameWithParamFullOfSpaces() throws Exception {
        mockMvc.perform(get("/user-info/small"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.publicName", is("admin admin")));

        mockMvc.perform(patch("/change/public-name").param("publicName", "         "))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field cannot be empty"));

        mockMvc.perform(get("/user-info/small"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.publicName", is("admin admin")));
    }

    //Change password tests

    @Test
    void changePassword() throws Exception {
        MultiValueMap<String, String> loginInfo = new LinkedMultiValueMap<>();
        loginInfo.add("username", "test@admin.com");
        loginInfo.add("password", "test");

        mockMvc.perform(post("/login").params(loginInfo))
                .andExpect(status().is(200));

        MultiValueMap<String, String> newPasswords = new LinkedMultiValueMap<>();
        newPasswords.add("oldPassword", "test");
        newPasswords.add("newPassword", "test1");

        mockMvc.perform(patch("/change/password").with(user("admin")).params(newPasswords))
                .andExpect(status().is(200));

        mockMvc.perform(post("/login").params(loginInfo))
                .andExpect(status().is(404))
                .andExpect(content().string("Bad credentials"));

        MultiValueMap<String, String> newOldPasswords = new LinkedMultiValueMap<>();
        newOldPasswords.add("oldPassword", "test1");
        newOldPasswords.add("newPassword", "test");

        mockMvc.perform(patch("/change/password").with(user("admin")).params(newOldPasswords))
                .andExpect(status().is(200));

        mockMvc.perform(post("/login").params(loginInfo))
                .andExpect(status().is(200));
    }

    @Test
    void changePasswordWithOldWrongPassword() throws Exception {
        MultiValueMap<String, String> loginInfo = new LinkedMultiValueMap<>();
        loginInfo.add("username", "test@admin.com");
        loginInfo.add("password", "test");

        mockMvc.perform(post("/login").params(loginInfo))
                .andExpect(status().is(200));

        MultiValueMap<String, String> newPasswords = new LinkedMultiValueMap<>();
        newPasswords.add("oldPassword", "tes");
        newPasswords.add("newPassword", "test1");

        mockMvc.perform(patch("/change/password").params(newPasswords).with(user("admin")))
                .andExpect(status().is(403));

        mockMvc.perform(post("/login").params(loginInfo))
                .andExpect(status().is(200));
    }

    @Test
    void changePasswordWithNoParams() throws Exception {
        MultiValueMap<String, String> loginInfo = new LinkedMultiValueMap<>();
        loginInfo.add("username", "test@admin.com");
        loginInfo.add("password", "test");

        mockMvc.perform(post("/login").params(loginInfo))
                .andExpect(status().is(200));

        mockMvc.perform(patch("/change/password").with(user("admin")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field cannot be empty"));

        mockMvc.perform(post("/login").params(loginInfo))
                .andExpect(status().is(200));
    }

    @Test
    void changePasswordWithParamsFullOfSpaces() throws Exception {
        MultiValueMap<String, String> loginInfo = new LinkedMultiValueMap<>();
        loginInfo.add("username", "test@admin.com");
        loginInfo.add("password", "test");

        mockMvc.perform(post("/login").params(loginInfo))
                .andExpect(status().is(200));

        MultiValueMap<String, String> newPasswords = new LinkedMultiValueMap<>();
        newPasswords.add("oldPassword", "       ");
        newPasswords.add("newPassword", "       ");

        mockMvc.perform(patch("/change/password").with(user("admin")).params(newPasswords))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field cannot be empty"));

        mockMvc.perform(post("/login").params(loginInfo))
                .andExpect(status().is(200));
    }
}
