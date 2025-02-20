package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Profile("test")
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc
class EwmStatTest {

    private final MockMvc mockMvc;

    @Test
    void correctGitAndStatsSort() throws Exception {
        String endpointHitDto1 = "{"
                + "\"app\": \"ewm-main-service\","
                + "\"uri\": \"/events/1\","
                + "\"ip\": \"127.0.0.1\","
                + "\"timestamp\": \"2025-02-19 14:30:45\""
                + "}";

        String endpointHitDto2 = "{"
                + "\"app\": \"ewm-main-service\","
                + "\"uri\": \"/events/2\","
                + "\"ip\": \"127.0.0.1\","
                + "\"timestamp\": \"2025-02-19 14:30:46\""
                + "}";


        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/hit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(endpointHitDto1))
                    .andExpect(status().isCreated());
        }

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/hit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(endpointHitDto2))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/stats")
                        .param("start", "2025-02-19 14:30:45")
                        .param("end", "2025-02-19 14:30:46")
                        .param("uris", "/events/2", "/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri").value("/events/2"))
                .andExpect(jsonPath("$[0].hits").value(3))
                .andExpect(jsonPath("$[1].uri").value("/events/1"))
                .andExpect(jsonPath("$[1].hits").value(2));
    }
}