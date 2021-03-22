package com.bossabox.supervuttr.controller;

import com.bossabox.supervuttr.controller.dtos.UserDTO;
import com.bossabox.supervuttr.data.AppUser;
import com.bossabox.supervuttr.security.UserDetailsServiceImpl;
import com.bossabox.supervuttr.service.UserService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)

@AutoConfigureJsonTesters
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication auth;

    @Autowired
    private JacksonTester<UserDTO> dtoJson;

    private UserDTO dto = new UserDTO("notId", "username67",
            "my_password", "my_password" );

    private AppUser user = new AppUser("writtenId", "username67", "hashedpassword");


    @Test
    public void test_createUser() throws Exception {
        when(userService.createUser(any())).thenReturn(user);

        var json = new JSONObject();
        json.put("id", dto.getId());
        json.put("username", dto.getUsername());

        mvc.perform(post(new URI("/api/user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isBadRequest());


        json.put("password", dto.getPassword());
        json.put("confirmPassword", dto.getConfirmPassword());
        var content = mvc.perform(post(new URI("/api/user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService, atLeastOnce()).createUser(any());

        // Ensures no leakage of passwords
        assertFalse(content.contains("password"));
        assertFalse(content.contains("confirm"));
        assertTrue(content.contains("username67"));

        json.put("confirmPassword", "not confirmed");
        mvc.perform(post(new URI("/api/user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString()))
                .andExpect(status().isBadRequest());

        verify(userService, atMost(1)).createUser(any());
    }

    @Test
    @WithMockUser("username67")
    public void test_deleteUser() throws Exception {
        mvc.perform(delete(new URI("/api/user/" + "anyuser")))
                .andExpect(status().isForbidden());

        mvc.perform(delete(new URI("/api/user/" + dto.getUsername())))
                .andExpect(status().isNoContent());
    }

}
