package com.app.prompt_engg.models;

import java.util.List;

public class ScenarioAnalysisRequest {
    private String scenario;
    private List<String> constraints;
	public String getScenario() {
		return scenario;
	}
	public void setScenario(String scenario) {
		this.scenario = scenario;
	}
	public List<String> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<String> constraints) {
		this.constraints = constraints;
	}
    
    
}

