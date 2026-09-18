package com.challenge.add2num;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
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

            @Test
            void streamsEveryStepProgressAndFinalResult() throws Exception {
            MvcResult creation = mockMvc.perform(post("/api/calculations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"first\":\"95\",\"second\":\"7\"}"))
                .andExpect(status().isOk())
                .andReturn();
            JsonNode job = new ObjectMapper().readTree(creation.getResponse().getContentAsString());

            MvcResult progress = mockMvc.perform(get("/api/calculations/"
                    + job.get("id").asText() + "/progress")
                    .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(request().asyncStarted())
                .andReturn();

            mockMvc.perform(asyncDispatch(progress))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                    containsString("event:step"),
                    containsString("\"percent\":50"),
                    containsString("\"percent\":100"),
                    containsString("event:complete"),
                    containsString("\"result\":\"102\""),
                    containsString("\"finalCarry\":1"))));
            }
}
