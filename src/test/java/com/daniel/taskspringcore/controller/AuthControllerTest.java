package com.daniel.taskspringcore.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.daniel.taskspringcore.exception.AuthenticationException;
import com.daniel.taskspringcore.facade.GymFacade;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GymFacade facade;

    @Test
    void loginReturns200() throws Exception {
        mockMvc.perform(get("/api/auth/login")
                        .param("username", "John.Smith")
                        .param("password", "pw"))
                .andExpect(status().isOk());

        verify(facade).login("John.Smith", "pw");
    }

    @Test
    void loginWithBadCredentialsReturns401() throws Exception {
        doThrow(new AuthenticationException("bad")).when(facade).login("John.Smith", "wrong");

        mockMvc.perform(get("/api/auth/login")
                        .param("username", "John.Smith")
                        .param("password", "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginMissingParamReturns400() throws Exception {
        mockMvc.perform(get("/api/auth/login").param("username", "John.Smith"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeLoginReturns200() throws Exception {
        mockMvc.perform(put("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"John.Smith\",\"oldPassword\":\"old\",\"newPassword\":\"new\"}"))
                .andExpect(status().isOk());

        verify(facade).changePassword("John.Smith", "old", "new");
    }

    @Test
    void changeLoginRejectsMissingNewPassword() throws Exception {
        mockMvc.perform(put("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"John.Smith\",\"oldPassword\":\"old\"}"))
                .andExpect(status().isBadRequest());
    }
}
