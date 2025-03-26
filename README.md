# Prompt-Engineering

## Overview

Prompt-Engineering is a full-stack AI-powered application that helps users analyze business scenarios by generating strategic insights. It allows users to input a project scenario and constraints, and then leverages AI to return:

1. A brief summary of the scenario  
2. A list of potential pitfalls or risks  
3. A list of proposed strategies  
4. A list of recommended resources  
5. A short disclaimer  

The application consists of:
- **Backend**: A Spring Boot application that handles prompt creation and communication with AI services.
- **Frontend**: A React application that provides a user-friendly interface for input and displaying the analysis.


## Tech Stack

- **Backend**: Java 17, Spring Boot, OpenAI API (or any pluggable AI model)
- **Frontend**: React, HTML, CSS
- **Build Tools**: Maven, npm


## Backend Setup

### Prerequisites
- Java 17+
- Maven
- OpenAI API Key

** Place the API Key in the backend/src/main/resources/application.properties file.**
**You can also find the prompt in backend/src/main/resources/application.properties file. When editing make sure you place `[scenarios]` and `[constraints]` for scenarios and constraints tpo place them dynamically**

### Steps to Run

```bash
# Navigate to the backend directory
cd backend

# Build the project
mvn clean install

# Run the application
java -jar target/prompt-engineering-backend.jar
```
Backend starts on http://localhost:8080 and consists of endpoint http://localhost:8080/analyser/analyser
- POST
- request sample: {
                    "scenario": "Our team has a new client project with a tight deadline and limited budget.",
                    "constraints": `[
                        "Budget: $10,000",
                        "Deadline: 6 weeks",
                        "Team of 3 developers"
                    ]`
                  }
- Response Sample: {
    "scenarioSummary": "Our team has secured a new client project with a strict six-week deadline and a budget cap of $10,000. The development team consists of three                                 members responsible for delivering the project efficiently.",
    "potentialPitfalls": `[
        "Scope creep due to unclear requirements",
        "Team burnout from tight deadlines",
        "Insufficient budget for unexpected challenges",
        "Limited resources affecting quality and speed"
    ]`,
    "proposedStrategies": `[
        "Implementing agile methodologies for iterative progress",
        "Setting clear, prioritized tasks with regular client check-ins",
        "Utilizing open-source tools to save costs",
        "Conducting regular team meetings to manage workload and morale"
    ]`,
    "recommendedResources": `[
        "Project management software like Trello or Asana",
        "Open-source libraries relevant to the project",
        "Time tracking tools to monitor work hours",
        "Access to online development communities for support"
    ]`,
    "disclaimer": "This guidance is based on general practices and should be tailored to the specific details of the project."
}

## Frontend Setup

### Prerequisites
- Node.js v14+
- npm

### Steps to Run

```bash
# Navigate to the frontend directory
cd frontend

# Install dependencies
npm install

# Start the frontend
npm start
```
