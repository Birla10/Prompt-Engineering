package com.app.prompt_engg.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.app.prompt_engg.controllers.ScenarioAnalysisController;
import com.app.prompt_engg.models.ScenarioAnalysisRequest;
import com.app.prompt_engg.models.ScenarioAnalysisResponse;
import com.app.prompt_engg.services.AiService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for {@link ScenarioAnalysisController}.
 * This class uses MockMvc to emulate HTTP requests and verifies responses for different test scenarios.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ScenarioAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private AiService aiService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Tests the normal scenario analysis.
     * Verifies that a valid input returns the expected result.
     */
    @Test
    public void testAnalyseScenario() throws Exception {
        ScenarioAnalysisRequest request = new ScenarioAnalysisRequest();
        
        List<String> constraints = new ArrayList<>();
        constraints.add("Budget: $10,000");
        constraints.add("Deadline: 6 weeks");
        constraints.add("Team of 3 developers");
        
        String scenario = "Our team has a new client project with a tight deadline and limited budget.";
        
        request.setConstraints(constraints);
        request.setScenario(scenario);

        ScenarioAnalysisResponse expectedResponse = new ScenarioAnalysisResponse();
        expectedResponse.setDisclaimer("Sample discliamer");
        expectedResponse.setScenarioSummary("Sample summary");
        expectedResponse.setPotentialPitfalls(new ArrayList<>());
        expectedResponse.setProposedStrategies(new ArrayList<>());
        expectedResponse.setRecommendedResources(new ArrayList<>());
        
        when(aiService.generateAnalysis(any(ScenarioAnalysisRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(
                post("/analyser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    /**
     * Tests the scenario analysis with invalid input.
     * Verifies that missing required input triggers a 400 Bad Request response.
     */
    @Test
    public void testAnalyseScenario_InvalidInput() throws Exception {
        ScenarioAnalysisRequest emptyRequest = new ScenarioAnalysisRequest();

        mockMvc.perform(
                post("/analyser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests the scenario analysis when the service returns a null response.
     * Expects a 200 OK response with a response body of "null".
     */
    @Test
    public void testAnalyseScenario_NullResponse() throws Exception {
        ScenarioAnalysisRequest request = new ScenarioAnalysisRequest();
        List<String> constraints = new ArrayList<>();
        constraints.add("Budget: $10,000");
        constraints.add("Deadline: 6 weeks");
        constraints.add("Team of 3 developers");
        
        String scenario = "Our team has a new client project with a tight deadline and limited budget.";
        
        request.setConstraints(constraints);
        request.setScenario(scenario);

        when(aiService.generateAnalysis(any(ScenarioAnalysisRequest.class))).thenReturn(null);

        mockMvc.perform(
                post("/analyser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("null"));
    }

    /**
     * Tests the scenario analysis when the service throws an exception.
     * Verifies that an internal server error (500) is returned.
     */
    @Test
    public void testAnalyseScenario_ServiceException() throws Exception {
        ScenarioAnalysisRequest request = new ScenarioAnalysisRequest();
        List<String> constraints = new ArrayList<>();
        constraints.add("Budget: $10,000");
        constraints.add("Deadline: 6 weeks");
        constraints.add("Team of 3 developers");
        
        String scenario = "Our team has a new client project with a tight deadline and limited budget.";
        
        request.setConstraints(constraints);
        request.setScenario(scenario);

        when(aiService.generateAnalysis(any(ScenarioAnalysisRequest.class)))
                .thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(
                post("/analyser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
