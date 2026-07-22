package com.daniel.taskspringcore.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.daniel.taskspringcore.dto.TrainingTypeDTO;
import com.daniel.taskspringcore.facade.GymFacade;

@WebMvcTest(TrainingTypeController.class)
class TrainingTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GymFacade facade;

    @Test
    void getTrainingTypesReturnsListNoAuthRequired() throws Exception {
        when(facade.getTrainingTypes()).thenReturn(List.of(
                new TrainingTypeDTO(1L, "Cardio"), new TrainingTypeDTO(2L, "Strength")));

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Cardio"))
                .andExpect(jsonPath("$[1].name").value("Strength"));
    }
}
