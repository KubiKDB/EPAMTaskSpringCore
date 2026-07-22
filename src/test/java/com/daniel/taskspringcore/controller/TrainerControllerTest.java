package com.daniel.taskspringcore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.daniel.taskspringcore.dto.TrainerProfileDTO;
import com.daniel.taskspringcore.dto.UserCredentialsDTO;
import com.daniel.taskspringcore.facade.GymFacade;
import com.daniel.taskspringcore.web.ApiConstants;

@WebMvcTest(TrainerController.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GymFacade facade;

    @Test
    void registerReturns201AndCredentials() throws Exception {
        when(facade.createTrainer(any())).thenReturn(new UserCredentialsDTO("Anna.Jones", "pw12345678"));

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Anna\",\"lastName\":\"Jones\",\"specialization\":\"Yoga\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Anna.Jones"));
    }

    @Test
    void registerRejectsMissingSpecialization() throws Exception {
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Anna\",\"lastName\":\"Jones\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProfileReturnsTraineesList() throws Exception {
        when(facade.getTrainerByUsername("a", "pw", "Anna.Jones"))
                .thenReturn(new TrainerProfileDTO("Anna.Jones", "Anna", "Jones", true, "Yoga", List.of()));

        mockMvc.perform(get("/api/trainers/Anna.Jones")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specialization").value("Yoga"))
                .andExpect(jsonPath("$.trainees").isArray());
    }

    @Test
    void updateReturnsProfile() throws Exception {
        when(facade.updateTrainer(eq("a"), eq("pw"), any()))
                .thenReturn(new TrainerProfileDTO("Anna.Jones", "Annie", "Jones", false, "Yoga", List.of()));

        mockMvc.perform(put("/api/trainers/Anna.Jones")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Annie\",\"lastName\":\"Jones\",\"isActive\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Annie"))
                .andExpect(jsonPath("$.specialization").value("Yoga"));

        verify(facade).updateTrainer(eq("a"), eq("pw"), any());
    }

    @Test
    void updateRejectsMissingIsActive() throws Exception {
        mockMvc.perform(put("/api/trainers/Anna.Jones")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Annie\",\"lastName\":\"Jones\"}"))
                .andExpect(status().isBadRequest());
    }
}
