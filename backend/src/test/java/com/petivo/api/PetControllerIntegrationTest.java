package com.petivo.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petivo.api.entity.Role;
import com.petivo.api.entity.User;
import com.petivo.api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PetControllerIntegrationTest {

    private static final String TEST_PASSWORD = "pet-integration-test-password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User owner;
    private User otherUser;

    @BeforeEach
    void createUsers() {
        String suffix = UUID.randomUUID().toString();
        owner = userRepository.save(new User(
                "pet-owner", "pet-owner-" + suffix + "@petivo.test",
                passwordEncoder.encode(TEST_PASSWORD), Role.USER));
        otherUser = userRepository.save(new User(
                "other-user", "other-user-" + suffix + "@petivo.test",
                passwordEncoder.encode(TEST_PASSWORD), Role.USER));
    }

    @AfterEach
    void removeUsers() {
        if (owner != null) {
            userRepository.deleteById(owner.getId());
        }
        if (otherUser != null) {
            userRepository.deleteById(otherUser.getId());
        }
    }

    @Test
    void petEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Milo\",\"type\":\"Cat\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usersCanCreateAndListOnlyTheirOwnPets() throws Exception {
        String ownerToken = login(owner);
        String otherUserToken = login(otherUser);

        String petResponse = mockMvc.perform(post("/api/pets")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Milo\",\"type\":\"Cat\",\"breed\":\"Tabby\",\"age\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId", is(owner.getId().intValue())))
                .andExpect(jsonPath("$.ownerName", is(owner.getName())))
                .andExpect(jsonPath("$.name", is("Milo")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long petId = objectMapper.readTree(petResponse).get("id").asLong();

        mockMvc.perform(post("/api/pets")
                        .header("Authorization", "Bearer " + otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Luna\",\"type\":\"Dog\",\"breed\":\"Beagle\",\"age\":4}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/pets")
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is((int) petId)))
                .andExpect(jsonPath("$[0].name", is("Milo")))
                .andExpect(jsonPath("$[0].ownerName", is(owner.getName())));

        mockMvc.perform(get("/api/pets")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Luna")));

        mockMvc.perform(get("/api/pets/{id}", petId)
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isForbidden());
    }

    private String login(User user) throws Exception {
        String loginRequest = objectMapper.writeValueAsString(
                new LoginPayload("user", user.getEmail(), TEST_PASSWORD));
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("token").asText();
    }

    private record LoginPayload(String role, String username, String password) {
    }
}
