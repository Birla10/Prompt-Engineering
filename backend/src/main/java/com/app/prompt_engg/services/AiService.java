package com.app.prompt_engg.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.app.prompt_engg.exceptions.InvalidScenarioException;
import com.app.prompt_engg.models.ScenarioAnalysisRequest;
import com.app.prompt_engg.models.ScenarioAnalysisResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonField;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputItem;

/**
 * Service class for handling AI-based scenario analysis.
 */
@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

	@Value("${spring.ai.openai.api-key}")
	private String apiKey;
	
	@Value("${openai.prompt}")
	private String prompt;
	
	/**
	 * Generates an analysis based on the given scenario and constraints.
	 *
	 * @param request a ScenarioAnalysisRequest containing the scenario and constraints
	 * @return a ScenarioAnalysisResponse with the analysis details
	 * @throws Exception if the request is invalid or any error occurs during analysis generation
	 */
	public ScenarioAnalysisResponse generateAnalysis(ScenarioAnalysisRequest request) {
		// Log the start of request processing.
		logger.info("Starting analysis generation for scenario: {}", request != null ? request.getScenario() : "null");
		
		ScenarioAnalysisResponse result;
		try {

			if (request == null || request.getScenario() == null || request.getScenario().isBlank() || request.getConstraints().isEmpty()) {
				throw new InvalidScenarioException("Unable to process request, not a valid scenario");
			}

			// Log before building the prompt.
			logger.info("Building prompt for AI API call.");
			String prompt = buildPrompt(request);

			// Log before calling the API.
			logger.info("Calling AI API with built prompt.");
			String apiResponse = callApi(prompt);
			
			// Log the raw API response.
			logger.info("Received API response.");
			logger.info("API response: {}", apiResponse);
			result = parseApiResponse(apiResponse);

		} catch (Exception e) {
			// Log exception details.
			logger.error("Error generating response: {}", e.getMessage());
			throw new InvalidScenarioException("Unable to process request, error generating a response" + e.getMessage());
		}

		return result;
	}

	/**
	 * Parses the API response string into a ScenarioAnalysisResponse.
	 *
	 * @param apiResponse the raw API response in JSON format
	 * @return a ScenarioAnalysisResponse parsed from the API output
	 * @throws JsonMappingException if mapping fails
	 * @throws JsonProcessingException if JSON processing fails
	 */
	private ScenarioAnalysisResponse parseApiResponse(String apiResponse) throws JsonMappingException, JsonProcessingException {
		// Log the start of parsing.
		logger.info("Parsing API response.");
		
		int start = apiResponse.indexOf('{');
		int end = apiResponse.lastIndexOf('}') + 1;
		String trimmed = apiResponse.substring(start, end);
		
		JsonObject jsonObject = JsonParser.parseString(trimmed).getAsJsonObject();
		
		String summary = jsonObject.get("summary").getAsString();
	    String disclaimer = jsonObject.get("disclaimer").getAsString();

	    // Extract lists from JSON arrays
	    List<String> pitfalls = toStringList(jsonObject.getAsJsonArray("pitfalls_or_risks"));
	    List<String> strategies = toStringList(jsonObject.getAsJsonArray("proposed_strategies"));
	    List<String> resources = toStringList(jsonObject.getAsJsonArray("recommended_resources"));
		
		// Log successful parse.
		logger.info("API response parsed successfully.");
		return new ScenarioAnalysisResponse(summary, pitfalls, strategies, resources, disclaimer);
		
	}

	/**
	 * Converts a JsonArray into a List of Strings.
	 *
	 * @param array the JsonArray to convert
	 * @return a List containing the string representation of each element
	 */
	private static List<String> toStringList(JsonArray array) {
		List<String> list = new ArrayList<>();
		for (JsonElement el : array) {
			list.add(el.getAsString());
		}
		return list;
	}

	/**
	 * Builds the prompt string for the AI API using the scenario and constraints.
	 *
	 * @param request the ScenarioAnalysisRequest containing the scenario and constraints
	 * @return a formatted prompt string to be sent to the AI API
	 */
	private String buildPrompt(ScenarioAnalysisRequest request) {
		
		String final_prompt = prompt.replace("[scenarios]", request.getScenario())
				.replace("[constraints]", String.join(", ", request.getConstraints()));
		// Log the constructed prompt in info level.
		logger.info("Constructed prompt: {}", prompt);
		return final_prompt;
	}

	/**
	 * Calls the AI API with the given prompt and retrieves the response.
	 *
	 * @param prompt the prompt string for the AI API
	 * @return the response string from the AI
	 */
	protected String callApi(String prompt) {
		// Log before initializing the API client.
		logger.info("Initializing OpenAI client.");
		OpenAIClient client = OpenAIOkHttpClient.builder().apiKey(apiKey).build();

		ResponseCreateParams params = ResponseCreateParams.builder().input(prompt).model(ChatModel.GPT_4O).build();
		// Log the API call initiation.
		logger.info("Sending request to AI API.");
		Response response = client.responses().create(params);

		JsonField<List<ResponseOutputItem>> items = response._output();

		Optional<? extends List<ResponseOutputItem>> output = items.asKnown();

		JsonField<String> item = output.get().get(0).message().get().content().get(0).asOutputText()._text(); 
		
		// Log after receiving response content.
		logger.info("AI API call completed.");
		return item.toString();
	}
}
