package com.app.prompt_engg.services;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.app.prompt_engg.exceptions.InvalidScenarioException;
import com.app.prompt_engg.models.ScenarioAnalysisRequest;
import com.app.prompt_engg.models.ScenarioAnalysisResponse;

@SpringBootTest
@AutoConfigureMockMvc
class AiServiceTest {

    // Subclass the AiService to override callApi for testing
    class TestAiService extends AiService {
        private String fakeApiResponse;

        public void setFakeApiResponse(String fakeApiResponse) {
            this.fakeApiResponse = fakeApiResponse;
        }

        @Override
        protected String callApi(String prompt) {
            return fakeApiResponse;
        }
    }

    private TestAiService service;

    @BeforeEach
    void setup() {
        service = new TestAiService();
        // set a dummy API key so that production code does not complain.
        // Reflection or setter may be used if needed.
        // Here we assume it's not used since callApi is overridden.
    }

    @Test
    void testGenerateAnalysis_NullRequest() {
        Assertions.assertThrows(InvalidScenarioException.class, () -> {
            service.generateAnalysis(null);
        });
    }

    @Test
    void testGenerateAnalysis_NullScenario() {
        ScenarioAnalysisRequest request = new ScenarioAnalysisRequest();
        request.setScenario(null);
        request.setConstraints(List.of("constraint1"));
        Assertions.assertThrows(InvalidScenarioException.class, () -> {
            service.generateAnalysis(request);
        });
    }

    @Test
    void testGenerateAnalysis_BlankScenario() {
        ScenarioAnalysisRequest request = new ScenarioAnalysisRequest();
        request.setScenario("   ");
        request.setConstraints(List.of("constraint1"));
        Assertions.assertThrows(InvalidScenarioException.class, () -> {
            service.generateAnalysis(request);
        });
    }

    @Test
    void testGenerateAnalysis_EmptyConstraints() {
        ScenarioAnalysisRequest request = new ScenarioAnalysisRequest();
        request.setScenario("A valid scenario");
        request.setConstraints(Collections.emptyList());
        Assertions.assertThrows(InvalidScenarioException.class, () -> {
            service.generateAnalysis(request);
        });
    }

    @Test
    void testGenerateAnalysis_InvalidApiResponse() {
        ScenarioAnalysisRequest request = new ScenarioAnalysisRequest();
        request.setScenario("A valid scenario");
        request.setConstraints(List.of("constraint1"));
        // set an invalid JSON to force a parsing exception
        service.setFakeApiResponse("Invalid JSON Response");

        Assertions.assertThrows(Exception.class, () -> {
            service.generateAnalysis(request);
        });
    }

    @Test
    void testGenerateAnalysis_ValidApiResponse() {
        ScenarioAnalysisRequest request = new ScenarioAnalysisRequest();
        request.setScenario("A valid scenario");
        request.setConstraints(List.of("constraint1", "constraint2"));

        // Fake API response in required JSON format
        String fakeResponse = "{"
                + "\"summary\":\"Test summary\","
                + "\"pitfalls_or_risks\":[\"risk1\", \"risk2\"],"
                + "\"proposed_strategies\":[\"strategy1\"],"
                + "\"recommended_resources\":[\"resource1\"],"
                + "\"disclaimer\":\"Test disclaimer\""
                + "}";
        service.setFakeApiResponse("...some prefix... " + fakeResponse + " ...some suffix...");
        
        ScenarioAnalysisResponse response = service.generateAnalysis(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Test summary", response.getScenarioSummary());
        Assertions.assertEquals(List.of("risk1", "risk2"), response.getPotentialPitfalls());
        Assertions.assertEquals(List.of("strategy1"), response.getProposedStrategies());
        Assertions.assertEquals(List.of("resource1"), response.getRecommendedResources());
        Assertions.assertEquals("Test disclaimer", response.getDisclaimer());
    }
}
