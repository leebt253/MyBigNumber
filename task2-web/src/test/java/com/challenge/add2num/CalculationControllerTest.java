package com.challenge.add2num;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculationController.class)
@Import(CalculationController.class)
class CalculationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsCalculationJob() throws Exception {
        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"first\":\"95\",\"second\":\"7\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isString());
    }
}
