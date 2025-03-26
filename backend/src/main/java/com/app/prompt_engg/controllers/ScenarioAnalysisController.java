package com.app.prompt_engg.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.prompt_engg.models.ScenarioAnalysisRequest;
import com.app.prompt_engg.models.ScenarioAnalysisResponse;
import com.app.prompt_engg.services.AiService;

/**
 * REST controller for managing scenario analyses.
 */
@RestController
@Validated
public class ScenarioAnalysisController {

	@Autowired
	private AiService aiService;

	private static final Logger log = LoggerFactory.getLogger(ScenarioAnalysisController.class);

	/**
	 * Analyzes the provided scenario using AI-generated analysis.
	 *
	 * @param req the scenario analysis request containing input parameters
	 * @return a ResponseEntity containing the scenario analysis response
	 * @throws Exception if an error occurs during analysis
	 */
	@PostMapping("/analyser")
	public ResponseEntity<ScenarioAnalysisResponse> analyseScenario(@RequestBody ScenarioAnalysisRequest req){

		// Invoke AI service to process the analysis request
		ScenarioAnalysisResponse response = aiService.generateAnalysis(req);
		
		// Log successful generation of the analysis response
		log.info("Successfully created AI Response: {}", response);
		
		// Return the analysis response wrapped in a ResponseEntity
		return ResponseEntity.ok(response);
	}

}
