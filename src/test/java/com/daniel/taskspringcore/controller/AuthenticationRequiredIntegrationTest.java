package com.daniel.taskspringcore.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.daniel.taskspringcore.web.ApiConstants;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationRequiredIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void creatingProfilesNeedsNoCredentials() throws Exception {
        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Open\",\"lastName\":\"Access\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Open\",\"lastName\":\"Trainer\",\"specialization\":\"Cardio\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void readingTheTrainingTypeReferenceListNeedsNoCredentials() throws Exception {
        mockMvc.perform(get("/api/training-types")).andExpect(status().isOk());
    }

    @Test
    void everyOtherOperationIsRejectedWithBadCredentials() throws Exception {
        assertRejected(get("/api/trainees/Open.Access"));
        assertRejected(get("/api/trainees/Open.Access/trainings"));
        assertRejected(get("/api/trainees/Open.Access/unassigned-trainers"));
        assertRejected(get("/api/trainers/Open.Trainer"));
        assertRejected(get("/api/trainers/Open.Trainer/trainings"));
        assertRejected(delete("/api/trainees/Open.Access"));

        assertRejected(put("/api/trainees/Open.Access")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"A\",\"lastName\":\"B\",\"isActive\":true}"));
        assertRejected(put("/api/trainers/Open.Trainer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"A\",\"lastName\":\"B\",\"isActive\":true}"));
        assertRejected(put("/api/trainees/Open.Access/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"trainerUsernames\":[\"Open.Trainer\"]}"));
        assertRejected(patch("/api/trainees/Open.Access/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isActive\":false}"));
        assertRejected(post("/api/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"traineeUsername":"Open.Access","trainerUsername":"Open.Trainer",
                         "trainingName":"Session","trainingDate":"2026-01-01","trainingDuration":60}"""));
    }

    @Test
    void loginRejectsAWrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Open.Access\",\"password\":\"definitely-wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    private void assertRejected(MockHttpServletRequestBuilder request) throws Exception {
        mockMvc.perform(request
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "Open.Access")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "definitely-wrong"))
                .andExpect(status().isUnauthorized());
    }
}