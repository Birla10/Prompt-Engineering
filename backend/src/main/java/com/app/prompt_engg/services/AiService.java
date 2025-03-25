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

@Service
public class AiService {

	@Value("${spring.ai.openai.api-key}")
	private String apiKey;

	public ScenarioAnalysisResponse generateAnalysis(ScenarioAnalysisRequest request) throws Exception {
		
		ScenarioAnalysisResponse result;
		try {

			if (request == null || request.getScenario().isBlank() || request.getConstraints().isEmpty()) {
				throw new Exception("Unable to process request, not a valid scenario");
			}

			String prompt = buildPrompt(request);
			String apiResponse = callApi(prompt);
			
			System.out.println(apiResponse);
			result = parseApiResponse(apiResponse);

		} catch (Exception e) {
			throw new InvalidScenarioException("Unable to process request, error generating a response" + e.getMessage());
		}

		return result;
	}

	private ScenarioAnalysisResponse parseApiResponse(String apiResponse) throws JsonMappingException, JsonProcessingException {
		
		JsonObject jsonObject = JsonParser.parseString(apiResponse).getAsJsonObject();
		
		 String summary = jsonObject.get("summary").getAsString();
	     String disclaimer = jsonObject.get("disclaimer").getAsString();

	     // Extract lists
	     List<String> pitfalls = toStringList(jsonObject.getAsJsonArray("pitfalls_or_risks"));
	     List<String> strategies = toStringList(jsonObject.getAsJsonArray("proposed_strategies"));
	     List<String> resources = toStringList(jsonObject.getAsJsonArray("recommended_resources"));
		
		return new ScenarioAnalysisResponse(summary, pitfalls, strategies, resources, disclaimer);
		
	}

	private static List<String> toStringList(JsonArray array) {
		List<String> list = new ArrayList<>();
		for (JsonElement el : array) {
			list.add(el.getAsString());
		}
		return list;
	}

	private String buildPrompt(ScenarioAnalysisRequest request) {
		// Create a thoughtful, detailed prompt
		// that instructs the AI to provide the structured output.

		return "Given the following scenario and constraints, generate response in a json format without any extra characters:\n" + "1. A brief summary of the scenario\n"
				+ "2. pitfalls_or_risks\n" + "3. proposed_strategies\n" + "4. recommended_resources\n"
				+ "5. One-sentence disclaimer\n\n" + "Scenario: " + request.getScenario() + "\n" + "Constraints: "
				+ String.join(", ", request.getConstraints());
	}

	private String callApi(String prompt) {

		String res = " ";

		OpenAIClient client = OpenAIOkHttpClient.builder().apiKey(apiKey).build();

		ResponseCreateParams params = ResponseCreateParams.builder().input(prompt).model(ChatModel.GPT_4O).build();
		Response response = client.responses().create(params);

		JsonField<List<ResponseOutputItem>> items = response._output();

		Optional<? extends List<ResponseOutputItem>> output = items.asKnown();

		JsonField<String> item = output.get().get(0).message().get().content().get(0).asOutputText()._text(); 
		
		return item.toString();
	}
}
