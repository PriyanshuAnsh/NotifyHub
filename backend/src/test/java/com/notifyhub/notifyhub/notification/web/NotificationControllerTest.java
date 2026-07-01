package com.notifyhub.notifyhub.notification.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.notifyhub.notifyhub.notification.domain.Notification;
import com.notifyhub.notifyhub.notification.service.NotificationNotFoundException;
import com.notifyhub.notifyhub.notification.service.NotificationService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock NotificationService service;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(new NotificationController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

    @Test
    void createReturns201WithLocation() throws Exception {
        Notification n = Notification.create("a@b.com", "Hi", "Body");
        when(service.create(anyString(), anyString(), anyString())).thenReturn(n);

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType("application/json")
                        .content("{\"toEmail\":\"a@b.com\",\"subject\":\"Hi\",\"body\":\"Body\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/notifications/" + n.getId()))
                .andExpect(jsonPath("$.id").value(n.getId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(service).create("a@b.com", "Hi", "Body");
    }

    @Test
    void createWithInvalidEmailReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/notifications")
                        .contentType("application/json")
                        .content("{\"toEmail\":\"\",\"subject\":\"Hi\",\"body\":\"Body\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("toEmail")));

        verify(service, never()).create(anyString(), anyString(), anyString());
    }

    @Test
    void getByIdReturnsNotification() throws Exception {
        Notification n = Notification.create("a@b.com", "Hi", "Body");
        when(service.getById(n.getId())).thenReturn(n);

        mockMvc.perform(get("/api/v1/notifications/{id}", n.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toEmail").value("a@b.com"));
    }

    @Test
    void getByIdReturns404WhenMissing() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.getById(eq(id))).thenThrow(new NotificationNotFoundException(id));

        mockMvc.perform(get("/api/v1/notifications/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void listReturnsAll() throws Exception {
        Notification n = Notification.create("a@b.com", "Hi", "Body");
        when(service.list()).thenReturn(List.of(n));

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(n.getId().toString()));
    }
}
