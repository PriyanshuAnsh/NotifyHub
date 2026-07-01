package com.notifyhub.notifyhub.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Exercises the real security filter chain end to end: the API-key filter, the
 * authorization rules, and the 401 entry point. Requires Postgres + RabbitMQ.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired MockMvc mockMvc;

    @Test
    void healthProbeIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    void apiRejectsMissingKeyWith401() throws Exception {
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Unauthorized")));
    }

    @Test
    void apiAcceptsValidKey() throws Exception {
        mockMvc.perform(get("/api/v1/notifications").header(ApiKeyAuthFilter.HEADER, "dev-local-key"))
                .andExpect(status().isOk());
    }
}
