package com.daniel.taskspringcore.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.daniel.taskspringcore.exception.EntityNotFoundException;
import com.daniel.taskspringcore.facade.GymFacade;
import com.daniel.taskspringcore.web.ApiConstants;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GymFacade facade;

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder patchStatus(String body) {
        return patch("/api/users/Any.User/status")
                .header(ApiConstants.AUTH_USERNAME_HEADER, "a")
                .header(ApiConstants.AUTH_PASSWORD_HEADER, "pw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }

    @Test
    void activatesAnyUserRegardlessOfSubtype() throws Exception {
        mockMvc.perform(patchStatus("{\"isActive\":true}")).andExpect(status().isOk());

        verify(facade).setUserActive("a", "pw", "Any.User", true);
    }

    @Test
    void deactivatesAnyUserRegardlessOfSubtype() throws Exception {
        mockMvc.perform(patchStatus("{\"isActive\":false}")).andExpect(status().isOk());

        verify(facade).setUserActive("a", "pw", "Any.User", false);
    }

    @Test
    void repeatingTheSameStateReturns409BecauseNotIdempotent() throws Exception {
        doThrow(new IllegalStateException("already active"))
                .when(facade).setUserActive("a", "pw", "Any.User", true);

        mockMvc.perform(patchStatus("{\"isActive\":true}")).andExpect(status().isConflict());
    }

    @Test
    void unknownUserReturns404() throws Exception {
        doThrow(new EntityNotFoundException("User not found: Any.User"))
                .when(facade).setUserActive("a", "pw", "Any.User", true);

        mockMvc.perform(patchStatus("{\"isActive\":true}")).andExpect(status().isNotFound());
    }

    @Test
    void missingIsActiveReturns400() throws Exception {
        mockMvc.perform(patchStatus("{}")).andExpect(status().isBadRequest());
    }

    @Test
    void missingAuthHeadersReturns400() throws Exception {
        mockMvc.perform(patch("/api/users/Any.User/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isActive\":true}"))
                .andExpect(status().isBadRequest());
    }
}
