import React, { useState } from 'react';
import "../index.css";
import "../css/UploadForm.css";

const UploadForm = () => {
  const [constraintsInput, setConstraintsInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState("");
  const [scenario, setScenario] = useState("");
  const [message, setMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    setLoading(true);
    setMessage("");

    const parsedConstraints = constraintsInput
    .split('/')
    .map(s => s.trim())
    .filter(Boolean); // remove empty strings
   
    // Process the form data and call backend API
    const backendUrl = "http://localhost:8080/analyser";
    console.log("Backend URL:", backendUrl);
    // const formData = new FormData();
    // formData.append('scenario', scenario);
    // formData.append('constraints', constraints);

    try {
      const res = await fetch(`${backendUrl}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          "scenario": scenario,
          "constraints": parsedConstraints
        })
      });
      // Handle the response as needed
      const result = await res.json();
      console.log("API response:", result);
      if (res.ok) {
        setLoading(false);
        setResponse(result);
        setMessage("Response generated successfully!");
      } else {
        setLoading(false);
        setMessage("Failed to generate response. Please try again.");
      }
    } catch (error) {
      console.error("Error during API call", error);
    }
  };

  return (
    <>
    <form onSubmit={handleSubmit} className="upload-form">
      <div className="form-group">
        <label>Scenario:</label>
        <input type="text" onChange={(e) => setScenario(e.target.value)} />
      </div>
      <div className="form-group">
        <label>Enter constraints along with keywords (Budget: / Deadline: / Number of developers: ):</label>
        <input 
          type="text" 
          value={constraintsInput}
          onChange={(e) => setConstraintsInput(e.target.value)}        
          placeholder='Budget: / Deadline: / Number of developers: '
        />
      </div>
      <button type="submit" className="submit-button" disabled={loading || !scenario.trim() || !constraintsInput.trim()}>
        Submit
      </button>
      {loading && <p className="loading-text">Generating response...</p>}
      {message && <p className="message-text">{message}</p>}
    </form>
    {response && (
      <div className="response-card">
        <h2>Here's what I got for you!!</h2>
        <h3>Scenario Summary</h3>
        <p>{response.scenarioSummary}</p>

        <h3>Potential Pitfalls</h3>
        <ul>
          {response.potentialPitfalls.map((item, idx) => (
            <li key={idx}>{item}</li>
          ))}
        </ul>

        <h3>Proposed Strategies</h3>
        <ul>
          {response.proposedStrategies.map((item, idx) => (
            <li key={idx}>{item}</li>
          ))}
        </ul>

        <h3>Recommended Resources</h3>
        <ul>
          {response.recommendedResources.map((item, idx) => (
            <li key={idx}>{item}</li>
          ))}
        </ul>

        <h4 className="disclaimer">Disclaimer</h4>
        <p>{response.disclaimer}</p>
      </div>
    )}
  </>
  );
};

export default UploadForm;