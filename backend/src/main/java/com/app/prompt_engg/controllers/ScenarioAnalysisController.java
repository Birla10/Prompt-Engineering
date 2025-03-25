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

@RestController
@Validated
public class ScenarioAnalysisController {

	@Autowired
	private AiService aiService;

	private static final Logger log = LoggerFactory.getLogger(ScenarioAnalysisController.class);

	/**
	 * Adds a Task to the Database based on the provided details by user
	 * 
	 * @param taskRequest
	 * @return
	 * @throws Exception 
	 */
	@PostMapping("/analyser")
	public ResponseEntity<ScenarioAnalysisResponse> analyseScenario(@RequestBody ScenarioAnalysisRequest req) throws Exception {

		ScenarioAnalysisResponse response = aiService.generateAnalysis(req);
		
		log.info("successfully created AI Response ", response);
		
		return ResponseEntity.ok(response);

	}

}
