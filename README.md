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

### Steps to Run

```bash
# Navigate to the backend directory
cd backend

# Build the project
mvn clean install

# Run the application
java -jar target/prompt-engineering-backend.jar
```

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
