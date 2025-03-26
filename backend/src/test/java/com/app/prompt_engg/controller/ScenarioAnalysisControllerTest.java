package com.app.prompt_engg.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.app.prompt_engg.controllers.ScenarioAnalysisController;
import com.app.prompt_engg.exceptions.InvalidScenarioException;
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
    
    @MockBean
    private AiService aiService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    ScenarioAnalysisRequest request = new ScenarioAnalysisRequest();
    
    ScenarioAnalysisResponse expectedResponse = new ScenarioAnalysisResponse();
    
    @BeforeEach
    public void setup() {
    	List<String> constraints = new ArrayList<>();
        constraints.add("Budget: $10,000");
        constraints.add("Deadline: 6 weeks");
        constraints.add("Team of 3 developers");
        
        String scenario = "Our team has a new client project with a tight deadline and limited budget.";
        
        request.setConstraints(constraints);
        request.setScenario(scenario);
 
        expectedResponse.setDisclaimer("Sample discliamer");
        expectedResponse.setScenarioSummary("Sample summary");
        expectedResponse.setPotentialPitfalls(List.of("Risk of burnout"));
        expectedResponse.setProposedStrategies(List.of("Use agile methodology"));
        expectedResponse.setRecommendedResources(List.of("Jira", "Trello"));
    }
    /**
     * Tests the normal scenario analysis.
     * Verifies that a valid input returns the expected result.
     */
    @Test
    public void testAnalyseScenario() throws Exception {

        when(aiService.generateAnalysis(any(ScenarioAnalysisRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(
                post("/analyser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disclaimer").isNotEmpty())
                .andExpect(jsonPath("$.scenarioSummary").isNotEmpty())
                .andExpect(jsonPath("$.potentialPitfalls.length()").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.proposedStrategies.length()").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.recommendedResources.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    /**
     * Tests the scenario analysis with invalid input.
     * Verifies that missing required input triggers a 400 Bad Request response.
     */
    @Test
    public void testAnalyseScenario_InvalidInput() throws Exception {
        ScenarioAnalysisRequest emptyRequest = new ScenarioAnalysisRequest();
        
        when(aiService.generateAnalysis(any(ScenarioAnalysisRequest.class)))
        .thenThrow(new InvalidScenarioException("Bad request"));

        mockMvc.perform(
                post("/analyser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests the scenario analysis when the service throws an exception.
     * Verifies that an internal server error (500) is returned.
     */
    @Test
    public void testAnalyseScenario_ServiceException() throws Exception {

        when(aiService.generateAnalysis(any(ScenarioAnalysisRequest.class)))
                .thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(
                post("/analyser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
