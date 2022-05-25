package com.messaging.messagingapp.ControllerTests;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.messaging.messagingapp.data.repositories.RoleRepository;
import com.messaging.messagingapp.data.repositories.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    //Register user tests

    @Test
    void registerValidUser() throws Exception {
        long registeredUsersBeforeTest = userRepository.count();

        MultiValueMap<String, String> newUser = new LinkedMultiValueMap<>();
        newUser.add("username", "newUser");
        newUser.add("password", "test@123");
        newUser.add("confirmPassword", "test@123");
        newUser.add("email", "newUser@test.com");
        newUser.add("publicName", "new user");


        mockMvc.perform(post("/admin/register")
                        .params(newUser)
                        .with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().is(302))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", hasSize(0)));

        Assertions.assertEquals(registeredUsersBeforeTest + 1, userRepository.count());

        mockMvc.perform(get("/user-info/small").with(user("newUser")))
                .andExpect(jsonPath("$.username", is("newUser")))
                .andExpect(jsonPath("$.publicName", is("new user")))
                .andExpect(jsonPath("$.admin", is(false)));

        MultiValueMap<String, String> loginNewUser = new LinkedMultiValueMap<>();
        loginNewUser.add("username", newUser.getFirst("email"));
        loginNewUser.add("password", newUser.getFirst("password"));

        mockMvc.perform(post("/login").params(loginNewUser))
                .andExpect(status().is(200));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void registerUserWithATakenUsername() throws Exception {
        long registeredUsersBeforeTest = userRepository.count();

        MultiValueMap<String, String> newUser = new LinkedMultiValueMap<>();
        newUser.add("username", "admin");
        newUser.add("password", "test@123");
        newUser.add("confirmPassword", "test@123");
        newUser.add("email", "newUser@test.com");
        newUser.add("publicName", "new user");

        mockMvc.perform(post("/admin/register").params(newUser))
                .andExpect(status().is(302))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", Matchers.hasSize(1)))
                .andExpect(flash().attribute("errors", Matchers.contains("Username is taken!")));

        Assertions.assertEquals(registeredUsersBeforeTest, userRepository.count());
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void registerUserWithATakenEmail() throws Exception {
        long registeredUsersBeforeTest = userRepository.count();

        MultiValueMap<String, String> newUser = new LinkedMultiValueMap<>();
        newUser.add("username", "newUser");
        newUser.add("password", "test@123");
        newUser.add("confirmPassword", "test@123");
        newUser.add("email", "test@admin.com");
        newUser.add("publicName", "new user");

        mockMvc.perform(post("/admin/register").params(newUser))
                .andExpect(status().is(302))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", Matchers.hasSize(1)))
                .andExpect(flash().attribute("errors", Matchers.contains("Email is taken!")));

        Assertions.assertEquals(registeredUsersBeforeTest, userRepository.count());
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void registerUserWithPasswordsThatDontMatch() throws Exception {
        long registeredUsersBeforeTest = userRepository.count();

        MultiValueMap<String, String> newUser = new LinkedMultiValueMap<>();
        newUser.add("username", "newUser");
        newUser.add("password", "test@123");
        newUser.add("confirmPassword", "test@456");
        newUser.add("email", "newUser@test.com");
        newUser.add("publicName", "new user");

        mockMvc.perform(post("/admin/register").params(newUser))
                .andExpect(status().is(302))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", Matchers.hasSize(1)))
                .andExpect(flash().attribute("errors", Matchers.contains("Passwords don't match")));

        Assertions.assertEquals(registeredUsersBeforeTest, userRepository.count());
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void registerUserWithEmptyFields() throws Exception {
        long registeredUsersBeforeTest = userRepository.count();

        MultiValueMap<String, String> newUser = new LinkedMultiValueMap<>();
        newUser.add("username", "");
        newUser.add("password", "");
        newUser.add("confirmPassword", "");
        newUser.add("email", "");
        newUser.add("publicName", "");

        mockMvc.perform(post("/admin/register").params(newUser))
                .andExpect(status().is(302))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", Matchers.hasSize(1)))
                .andExpect(flash().attribute("errors", Matchers.contains("Fields cannot be empty")));

        Assertions.assertEquals(registeredUsersBeforeTest, userRepository.count());
    }

    //Search user tests

    //By Id

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void searchForAValidUserById() throws Exception {
        MultiValueMap<String, String> searchParams = new LinkedMultiValueMap<>();
        searchParams.add("searchValue", "1");
        searchParams.add("searchType", "id");

        mockMvc.perform(get("/admin/search").params(searchParams))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("user-details"))
                .andExpect(flash().attributeExists("user"));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void searchForANonExistentUserById() throws Exception {
        MultiValueMap<String, String> searchParams = new LinkedMultiValueMap<>();
        searchParams.add("searchValue", "210");
        searchParams.add("searchType", "id");

        mockMvc.perform(get("/admin/search").params(searchParams))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("user-management"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", Matchers.contains("User not found")));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void searchByIdWithALetter() throws Exception {
        MultiValueMap<String, String> searchParams = new LinkedMultiValueMap<>();
        searchParams.add("searchValue", "a");
        searchParams.add("searchType", "id");

        mockMvc.perform(get("/admin/search").params(searchParams))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("user-management"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", Matchers.contains("Invalid search value")));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void searchByIdWithEmptySearchValue() throws Exception{
        MultiValueMap<String, String> searchParams = new LinkedMultiValueMap<>();
        searchParams.add("searchValue", "");
        searchParams.add("searchType", "id");

        mockMvc.perform(get("/admin/search").params(searchParams))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("user-management"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", Matchers.contains("Invalid search value")));
    }

    //By Username

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void searchOneUserByUsername() throws Exception {
        MultiValueMap<String, String> searchParams = new LinkedMultiValueMap<>();
        searchParams.add("searchValue", "admin");
        searchParams.add("searchType", "username");

        mockMvc.perform(get("/admin/search").params(searchParams))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("list"))
                .andExpect(flash().attributeExists("users"))
                .andExpect(flash().attribute("users", hasSize(1)));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void searchForManyUsersByUsername() throws Exception {
        MultiValueMap<String, String> searchParams = new LinkedMultiValueMap<>();
        searchParams.add("searchValue", "test");
        searchParams.add("searchType", "username");

        mockMvc.perform(get("/admin/search").params(searchParams))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("list"))
                .andExpect(flash().attributeExists("users"))
                .andExpect(flash().attribute("users", Matchers.hasSize(Matchers.greaterThan(1))));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void searchWithAnEmptySearchValue() throws Exception {
        MultiValueMap<String, String> searchParams = new LinkedMultiValueMap<>();
        searchParams.add("searchValue", "");
        searchParams.add("searchType", "username");

        mockMvc.perform(get("/admin/search").params(searchParams))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("user-management"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", Matchers.contains("Invalid search value")));
    }

    //Views tests

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void adminPanelView() throws Exception {
        mockMvc.perform(get("/admin/panel"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void registerView() throws Exception {
        mockMvc.perform(get("/admin/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errors"))
                .andExpect(model().attribute("errors", hasSize(0)));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void userManagementView() throws Exception {
        mockMvc.perform(get("/admin/user-management"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errors"))
                .andExpect(model().attribute("errors", hasSize(0)));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void usersListView() throws Exception {
        mockMvc.perform(get("/admin/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void userDetailsView() throws Exception {
        mockMvc.perform(get("/admin/user-details"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void expandValidUserDetailsView() throws Exception {
        mockMvc.perform(get("/admin/expand").param("id", "1"))
                .andExpect(status().is(302))
                .andExpect(flash().attributeExists("user"))
                .andExpect(redirectedUrl("user-details"));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"USER", "ADMIN"})
    void expandNonExistentUserDetailsView() throws Exception {
        mockMvc.perform(get("/admin/expand").param("id", "210"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("user-management"));
    }
}
