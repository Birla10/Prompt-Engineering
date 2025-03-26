package com.app.prompt_engg.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for handling AI-based scenario analysis.
 */
@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

	@Value("${spring.ai.openai.api-key}")
	private String apiKey;
	
	@Value("${spring.ai.openai.model}")
	private String apiModel;

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
			logger.debug("Building prompt for AI API call.");
			String prompt = buildPrompt(request);

			// Log before calling the API.
			logger.debug("Calling AI API with built prompt.");
			String apiResponse = callApi(prompt);
			
			// Log the raw API response.
			logger.info("Received API response.");
			logger.debug("API response: {}", apiResponse);
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
		logger.debug("Parsing API response.");
		
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
		logger.debug("API response parsed successfully.");
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
		// Construct a detailed prompt for structured output
		String prompt = "Given the following scenario and constraints, generate:\n" + 
		       "1. A brief summary of the scenario\n" +
		       "2. List of pitfalls_or_risks\n" + 
		       "3. List of proposed_strategies\n" + 
		       "4. List of recommended_resources\n" +
		       "5. Short disclaimer\n\n" + 
		       "Scenario: " + request.getScenario() + "\n" + 
		       "Constraints: " + String.join(", ", request.getConstraints()) +
		       " Provide the response in a json format with the mentioned keys";
		// Log the constructed prompt in debug level.
		logger.debug("Constructed prompt: {}", prompt);
		return prompt;
	}

	/**
	 * Calls the AI API with the given prompt and retrieves the response.
	 *
	 * @param prompt the prompt string for the AI API
	 * @return the response string from the AI
	 */
	protected String callApi(String prompt) {
		// Log before initializing the API client.
		logger.debug("Initializing OpenAI client.");
		OpenAIClient client = OpenAIOkHttpClient.builder().apiKey(apiKey).build();

		ResponseCreateParams params = ResponseCreateParams.builder().input(prompt).model(apiModel).build();
		// Log the API call initiation.
		logger.debug("Sending request to AI API.");
		Response response = client.responses().create(params);

		JsonField<List<ResponseOutputItem>> items = response._output();

		Optional<? extends List<ResponseOutputItem>> output = items.asKnown();

		JsonField<String> item = output.get().get(0).message().get().content().get(0).asOutputText()._text(); 
		
		// Log after receiving response content.
		logger.debug("AI API call completed.");
		return item.toString();
	}
}
