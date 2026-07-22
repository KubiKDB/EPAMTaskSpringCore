package com.daniel.taskspringcore.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.daniel.taskspringcore.web.ApiConstants;

/**
 * End-to-end smoke test: boots the full application context against H2 and exercises the real
 * filter chain, controllers, services and persistence, plus the springdoc OpenAPI document.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void trainingTypesAreSeededAndReturnTransactionIdHeader() throws Exception {
        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(header().exists(ApiConstants.TRANSACTION_ID_HEADER))
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void registerThenLoginRoundTrip() throws Exception {
        String creds = mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Ivy\",\"lastName\":\"Bell\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Ivy.Bell"))
                .andReturn().getResponse().getContentAsString();

        String password = creds.replaceAll(".*\"password\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Ivy.Bell\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Ivy.Bell\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void openApiDocumentIsAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/trainees']").exists())
                .andExpect(jsonPath("$.info.title").value("Gym CRM REST API"));
    }
}
