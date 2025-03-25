package com.app.prompt_engg.models;

import java.util.List;

public class ScenarioAnalysisResponse {
    private String scenarioSummary;
    private List<String> potentialPitfalls;
    private List<String> proposedStrategies;
    private List<String> recommendedResources;
    private String disclaimer;
	
    public ScenarioAnalysisResponse(String scenarioSummary, List<String> potentialPitfalls,
			List<String> proposedStrategies, List<String> recommendedResources, String disclaimer) {
		super();
		this.scenarioSummary = scenarioSummary;
		this.potentialPitfalls = potentialPitfalls;
		this.proposedStrategies = proposedStrategies;
		this.recommendedResources = recommendedResources;
		this.disclaimer = disclaimer;
	}
    
	public String getScenarioSummary() {
		return scenarioSummary;
	}
	public void setScenarioSummary(String scenarioSummary) {
		this.scenarioSummary = scenarioSummary;
	}
	public List<String> getPotentialPitfalls() {
		return potentialPitfalls;
	}
	public void setPotentialPitfalls(List<String> potentialPitfalls) {
		this.potentialPitfalls = potentialPitfalls;
	}
	public List<String> getProposedStrategies() {
		return proposedStrategies;
	}
	public void setProposedStrategies(List<String> proposedStrategies) {
		this.proposedStrategies = proposedStrategies;
	}
	public List<String> getRecommendedResources() {
		return recommendedResources;
	}
	public void setRecommendedResources(List<String> recommendedResources) {
		this.recommendedResources = recommendedResources;
	}
	public String getDisclaimer() {
		return disclaimer;
	}
	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}
    
}
