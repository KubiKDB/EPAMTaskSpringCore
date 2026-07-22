package com.daniel.taskspringcore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.daniel.taskspringcore.exception.EntityNotFoundException;
import com.daniel.taskspringcore.facade.GymFacade;
import com.daniel.taskspringcore.web.ApiConstants;

@WebMvcTest(TrainingController.class)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GymFacade facade;

    private static final String BODY = "{\"traineeUsername\":\"John.Smith\",\"trainerUsername\":\"Anna.Jones\","
            + "\"trainingName\":\"Cardio\",\"trainingDate\":\"2026-07-20\",\"trainingDuration\":60}";

    @Test
    void addTrainingReturns200AndDerivesType() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk());

        verify(facade).addTraining("a", "pw", "John.Smith", "Anna.Jones", "Cardio",
                LocalDate.of(2026, 7, 20), 60);
    }

    @Test
    void addTrainingRejectsMissingFields() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"traineeUsername\":\"John.Smith\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addTrainingUnknownTraineeReturns404() throws Exception {
        doThrow(new EntityNotFoundException("Trainee not found"))
                .when(facade).addTraining(eq("a"), eq("pw"), any(), any(), any(), any(), any());

        mockMvc.perform(post("/api/trainings")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isNotFound());
    }
}
