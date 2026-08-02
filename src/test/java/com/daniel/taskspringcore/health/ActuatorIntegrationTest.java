package com.daniel.taskspringcore.health;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.daniel.taskspringcore.web.ApiConstants;

@SpringBootTest
@AutoConfigureMockMvc
class ActuatorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void ensureTheGymHasAnActiveTrainer() throws Exception {
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Health\",\"lastName\":\"Check\",\"specialization\":\"Yoga\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void healthEndpointReportsTheCustomIndicators() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.trainingTypeCatalog.status").value("UP"))
                .andExpect(jsonPath("$.components.trainingTypeCatalog.details.trainingTypes")
                        .value(Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.components.gymDatabase.status").value("UP"))
                .andExpect(jsonPath("$.components.gymDatabase.details.database").value("H2"))
                .andExpect(jsonPath("$.components.activeTrainer").exists());
    }

    @Test
    void healthEndpointStillReportsTheBuiltInIndicators() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.db.status").value("UP"))
                .andExpect(jsonPath("$.components.diskSpace.status").value("UP"));
    }

    @Test
    void prometheusEndpointExposesTheCustomMeters() throws Exception {
        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Metric\",\"lastName\":\"Probe\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("gym_profile_registrations_total")))
                .andExpect(content().string(Matchers.containsString("role=\"trainee\"")))
                .andExpect(content().string(Matchers.containsString("gym_trainees_active")))
                .andExpect(content().string(Matchers.containsString("gym_trainers_active")))
                .andExpect(content().string(Matchers.containsString("environment=\"test\"")));
    }

    @Test
    void prometheusEndpointRecordsAuthenticationOutcomes() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"No.Such.User\",\"password\":\"nope\"}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("gym_authentication_attempts_total")))
                .andExpect(content().string(Matchers.containsString("outcome=\"failure\"")))
                .andExpect(content().string(Matchers.containsString("gym_authentication_duration_seconds")));
    }

    @Test
    void prometheusEndpointExposesTheTrainingCounterUnderItsExpectedName() throws Exception {
        String trainee = register("/api/trainees", "{\"firstName\":\"Counter\",\"lastName\":\"Trainee\"}");
        String trainer = register("/api/trainers",
                "{\"firstName\":\"Counter\",\"lastName\":\"Trainer\",\"specialization\":\"Yoga\"}");

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(ApiConstants.AUTH_USERNAME_HEADER, username(trainee))
                        .header(ApiConstants.AUTH_PASSWORD_HEADER, password(trainee))
                        .content("""
                                {"traineeUsername":"%s","trainerUsername":"%s","trainingName":"Session",
                                 "trainingDate":"2026-08-05","trainingDuration":60}"""
                                .formatted(username(trainee), username(trainer))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("gym_trainings_total")))
                .andExpect(content().string(Matchers.containsString("type=\"Yoga\"")));
    }

    @Test
    void infoEndpointIsAvailable() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name").value("Gym CRM"));
    }

    private String register(String path, String body) throws Exception {
        return mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private static String username(String credentials) {
        return field(credentials, "username");
    }

    private static String password(String credentials) {
        return field(credentials, "password");
    }

    private static String field(String credentials, String name) {
        return credentials.replaceAll(".*\"" + name + "\":\"([^\"]+)\".*", "$1");
    }
}