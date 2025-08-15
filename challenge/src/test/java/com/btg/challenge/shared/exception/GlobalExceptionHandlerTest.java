package com.btg.challenge.shared.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");
        webRequest = new ServletWebRequest(request);
    }

    @Test
    public void whenHandleEntityNotFoundExceptionShouldReturnNotFoundResponse() {
        // Given
        String errorMessage = "Entity not found with id: 1";
        EntityNotFoundException exception = new EntityNotFoundException(errorMessage);

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityNotFoundException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        assertEquals(errorMessage, body.get("message"));
        assertNotNull(body.get("timestamp"));
        assertTrue(body.get("timestamp") instanceof LocalDateTime);
        
        LocalDateTime timestamp = (LocalDateTime) body.get("timestamp");
        assertTrue(timestamp.isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(timestamp.isAfter(LocalDateTime.now().minusSeconds(5)));
    }

    @Test
    public void whenHandleEntityNotFoundExceptionWithNullMessageShouldReturnDefaultMessage() {
        // Given
        EntityNotFoundException exception = new EntityNotFoundException((String) null);

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityNotFoundException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        assertNull(body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    public void whenHandleEntityNotFoundExceptionWithEmptyMessageShouldReturnDefaultMessage() {
        // Given
        String errorMessage = "";
        EntityNotFoundException exception = new EntityNotFoundException(errorMessage);

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleEntityNotFoundException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        assertEquals(errorMessage, body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    public void whenHandleResourceNotFoundExceptionShouldReturnNotFoundResponse() {
        // Given
        String errorMessage = "Resource not found with id: 1";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleResourceNotFoundException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        assertEquals(errorMessage, body.get("message"));
        assertNotNull(body.get("timestamp"));
        assertTrue(body.get("timestamp") instanceof LocalDateTime);
        
        LocalDateTime timestamp = (LocalDateTime) body.get("timestamp");
        assertTrue(timestamp.isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(timestamp.isAfter(LocalDateTime.now().minusSeconds(5)));
    }

    @Test
    public void whenHandleResourceNotFoundExceptionWithNullMessageShouldReturnDefaultMessage() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException(null);

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleResourceNotFoundException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        assertNull(body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    public void whenHandleResourceNotFoundExceptionWithEmptyMessageShouldReturnDefaultMessage() {
        // Given
        String errorMessage = "";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleResourceNotFoundException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        assertEquals(errorMessage, body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    public void whenHandleBothExceptionTypesShouldReturnConsistentStructure() {
        // Given
        String errorMessage = "Test error";
        EntityNotFoundException entityException = new EntityNotFoundException(errorMessage);
        ResourceNotFoundException resourceException = new ResourceNotFoundException(errorMessage);

        // When
        ResponseEntity<Object> entityResponse = globalExceptionHandler.handleEntityNotFoundException(entityException, webRequest);
        ResponseEntity<Object> resourceResponse = globalExceptionHandler.handleResourceNotFoundException(resourceException, webRequest);

        // Then
        assertEquals(entityResponse.getStatusCode(), resourceResponse.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> entityBody = (Map<String, Object>) entityResponse.getBody();
        @SuppressWarnings("unchecked")
        Map<String, Object> resourceBody = (Map<String, Object>) resourceResponse.getBody();
        
        assertNotNull(entityBody);
        assertNotNull(resourceBody);
        assertEquals(entityBody.keySet(), resourceBody.keySet());
        assertEquals(entityBody.get("message"), resourceBody.get("message"));
        assertTrue(entityBody.containsKey("timestamp"));
        assertTrue(resourceBody.containsKey("timestamp"));
    }

    @Test
    public void whenHandleExceptionShouldReturnResponseWithRequiredFields() {
        // Given
        String errorMessage = "Test error message";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleResourceNotFoundException(exception, webRequest);

        // Then
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        assertEquals(2, body.size());
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("message"));
        assertFalse(body.containsKey("status"));
        assertFalse(body.containsKey("error"));
    }
}
