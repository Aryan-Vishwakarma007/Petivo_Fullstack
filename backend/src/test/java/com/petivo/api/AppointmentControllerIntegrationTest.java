package com.petivo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petivo.api.dto.AppointmentRequestDto;
import com.petivo.api.entity.Role;
import com.petivo.api.entity.User;
import com.petivo.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AppointmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void createAppointmentUser() {
        userRepository.save(new User(
                "appointment-user", "appointment-user@petivo.test",
                passwordEncoder.encode("appointment-test-password"), Role.USER));
    }

    private String userToken() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(
                new LoginPayload("user", "appointment-user", "appointment-test-password"));
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void testGetAppointmentsEndpoint() throws Exception {
        String token = userToken();
        mockMvc.perform(get("/api/db/appointments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateAppointmentEndpoint() throws Exception {
        AppointmentRequestDto request = new AppointmentRequestDto();
        request.setPet("Leo");
        request.setOwner("Shreyash");
        request.setService("Basic Bath");
        request.setDate("2026-12-01");
        request.setStatus("Pending");
        String token = userToken();

        mockMvc.perform(post("/api/db/appointments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.pet", is("Leo")))
                .andExpect(jsonPath("$.service", is("Basic Bath")))
                .andExpect(jsonPath("$.date", is("2026-12-01")));
    }

    private record LoginPayload(String role, String username, String password) {
    }
}
