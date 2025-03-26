package com.app.prompt_engg.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.app.prompt_engg.models.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidScenarioException.class)
	public ResponseEntity<Response> handleUserAlreadyExistsException(InvalidScenarioException ex) {
		Response errorResponse = new Response(ex.getMessage(), HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Response> handleGenericException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new Response("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR));
	}
	
}
