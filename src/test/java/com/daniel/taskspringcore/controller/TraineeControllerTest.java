package com.daniel.taskspringcore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.daniel.taskspringcore.dto.TraineeDTO;
import com.daniel.taskspringcore.dto.TrainingDTO;
import com.daniel.taskspringcore.dto.UserCredentialsDTO;
import com.daniel.taskspringcore.exception.AuthenticationException;
import com.daniel.taskspringcore.exception.EntityNotFoundException;
import com.daniel.taskspringcore.facade.GymFacade;
import com.daniel.taskspringcore.web.ApiConstants;

@WebMvcTest(TraineeController.class)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GymFacade facade;

    @Test
    void registerReturns201AndCredentials() throws Exception {
        when(facade.createTrainee(any())).thenReturn(new UserCredentialsDTO("John.Smith", "pw12345678"));

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Smith\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Smith"))
                .andExpect(jsonPath("$.password").value("pw12345678"));
    }

    @Test
    void registerRejectsMissingFirstName() throws Exception {
        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lastName\":\"Smith\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProfileReturnsBodyAndPassesAuthHeaders() throws Exception {
        when(facade.getTraineeByUsername("John.Smith", "pw", "John.Smith"))
                .thenReturn(new TraineeDTO("John.Smith", "John", "Smith", null, "Main St", true, List.of()));

        mockMvc.perform(get("/api/trainees/John.Smith")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "John.Smith")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.active").value(true));

        verify(facade).getTraineeByUsername("John.Smith", "pw", "John.Smith");
    }

    @Test
    void getProfileWithoutAuthHeaderReturns400() throws Exception {
        mockMvc.perform(get("/api/trainees/John.Smith"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticationFailureReturns401() throws Exception {
        doThrow(new AuthenticationException("bad credentials"))
                .when(facade).getTraineeByUsername(eq("John.Smith"), eq("wrong"), eq("John.Smith"));

        mockMvc.perform(get("/api/trainees/John.Smith")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "John.Smith")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "wrong"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void notFoundReturns404() throws Exception {
        doThrow(new EntityNotFoundException("Trainee not found: ghost"))
                .when(facade).getTraineeByUsername("a", "pw", "ghost");

        mockMvc.perform(get("/api/trainees/ghost")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturnsProfile() throws Exception {
        when(facade.updateTrainee(eq("a"), eq("pw"), any()))
                .thenReturn(new TraineeDTO("John.Smith", "Johnny", "Smith", null, "New St", true, List.of()));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/trainees/John.Smith")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Johnny\",\"lastName\":\"Smith\",\"isActive\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"));
    }

    @Test
    void deleteReturns200() throws Exception {
        mockMvc.perform(delete("/api/trainees/John.Smith")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw"))
                .andExpect(status().isOk());

        verify(facade).deleteTraineeByUsername("a", "pw", "John.Smith");
    }

    @Test
    void activateStatusDelegates() throws Exception {
        mockMvc.perform(patch("/api/trainees/John.Smith/status")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isActive\":true}"))
                .andExpect(status().isOk());

        verify(facade).activateTrainee("a", "pw", "John.Smith");
    }

    @Test
    void deactivateTwiceReturns409() throws Exception {
        doThrow(new IllegalStateException("Trainee John.Smith is already inactive"))
                .when(facade).deactivateTrainee("a", "pw", "John.Smith");

        mockMvc.perform(patch("/api/trainees/John.Smith/status")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isActive\":false}"))
                .andExpect(status().isConflict());
    }

    @Test
    void getTrainingsMapsTrainerNameFromUsername() throws Exception {
        when(facade.getTraineeTrainings("a", "pw", "John.Smith", null, null, null, null))
                .thenReturn(List.of(new TrainingDTO(1L, "John.Smith", "Anna.Jones",
                        "Cardio", "Cardio", LocalDate.of(2026, 1, 2), 60)));

        mockMvc.perform(get("/api/trainees/John.Smith/trainings")
                        .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Cardio"))
                .andExpect(jsonPath("$[0].trainerName").value("Anna.Jones"));
    }
}
