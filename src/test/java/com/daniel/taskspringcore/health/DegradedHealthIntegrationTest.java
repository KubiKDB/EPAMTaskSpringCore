package com.daniel.taskspringcore.health;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.daniel.taskspringcore.service.GymStatsService;

@SpringBootTest
@AutoConfigureMockMvc
class DegradedHealthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymStatsService statsService;

    @Test
    void aGymWithNoActiveTrainerIsDegradedButStaysInRotation() throws Exception {
        when(statsService.countTrainingTypes()).thenReturn(3L);
        when(statsService.countUsers()).thenReturn(5L);
        when(statsService.countActiveTrainers()).thenReturn(0L);
        when(statsService.countActiveTrainees()).thenReturn(5L);

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OUT_OF_SERVICE"))
                .andExpect(jsonPath("$.components.activeTrainer.status").value("OUT_OF_SERVICE"));
    }

    @Test
    void anEmptyTrainingTypeCatalogueTakesTheInstanceOutOfRotation() throws Exception {
        when(statsService.countTrainingTypes()).thenReturn(0L);
        when(statsService.countUsers()).thenReturn(5L);
        when(statsService.countActiveTrainers()).thenReturn(2L);
        when(statsService.countActiveTrainees()).thenReturn(5L);

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.components.trainingTypeCatalog.status").value("DOWN"));
    }

    @Test
    void anUnusableSchemaTakesTheInstanceOutOfRotation() throws Exception {
        when(statsService.countTrainingTypes()).thenReturn(3L);
        when(statsService.countUsers()).thenThrow(new IllegalStateException("relation users does not exist"));
        when(statsService.countActiveTrainers()).thenReturn(2L);
        when(statsService.countActiveTrainees()).thenReturn(5L);

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.components.gymDatabase.status").value("DOWN"));
    }
}
