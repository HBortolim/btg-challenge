package com.btg.challenge.shared.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageServiceTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenGetMessageWithoutArgsShouldReturnTranslatedMessage() {
        // Given
        String key = "test.message";
        String expectedMessage = "Test message";

        when(messageSource.getMessage(key, new Object[0], Locale.getDefault()))
                .thenReturn(expectedMessage);

        // When
        String result = messageService.getMessage(key);

        // Then
        assertEquals(expectedMessage, result);
        verify(messageSource, times(1)).getMessage(key, new Object[0], Locale.getDefault());
    }

    @Test
    public void whenGetMessageWithSingleArgShouldReturnInterpolatedMessage() {
        // Given
        String key = "user.not.found";
        String username = "testuser";
        String expectedMessage = "User not found: testuser";

        when(messageSource.getMessage(key, new Object[]{username}, Locale.getDefault()))
                .thenReturn(expectedMessage);

        // When
        String result = messageService.getMessage(key, username);

        // Then
        assertEquals(expectedMessage, result);
        verify(messageSource, times(1)).getMessage(key, new Object[]{username}, Locale.getDefault());
    }

    @Test
    public void whenGetMessageWithMultipleArgsShouldReturnInterpolatedMessage() {
        // Given
        String key = "loan.created";
        String friendName = "John";
        String gameName = "God of War";
        String expectedMessage = "Loan created for John to borrow God of War";

        when(messageSource.getMessage(key, new Object[]{friendName, gameName}, Locale.getDefault()))
                .thenReturn(expectedMessage);

        // When
        String result = messageService.getMessage(key, friendName, gameName);

        // Then
        assertEquals(expectedMessage, result);
        verify(messageSource, times(1)).getMessage(key, new Object[]{friendName, gameName}, Locale.getDefault());
    }

    @Test
    public void whenGetMessageWithNullArgsShouldReturnMessageWithoutInterpolation() {
        // Given
        String key = "test.message";
        String expectedMessage = "Test message";

        when(messageSource.getMessage(key, new Object[]{null}, Locale.getDefault()))
                .thenReturn(expectedMessage);

        // When
        String result = messageService.getMessage(key, (Object) null);

        // Then
        assertEquals(expectedMessage, result);
        verify(messageSource, times(1)).getMessage(key, new Object[]{null}, Locale.getDefault());
    }

    @Test
    public void whenGetMessageWithEmptyArgsShouldReturnMessageWithoutInterpolation() {
        // Given
        String key = "test.message";
        String expectedMessage = "Test message";

        when(messageSource.getMessage(key, new Object[0], Locale.getDefault()))
                .thenReturn(expectedMessage);

        // When
        String result = messageService.getMessage(key, new Object[0]);

        // Then
        assertEquals(expectedMessage, result);
        verify(messageSource, times(1)).getMessage(key, new Object[0], Locale.getDefault());
    }

    @Test
    public void whenGetMessageWithNonExistentKeyShouldThrowNoSuchMessageException() {
        // Given
        String key = "nonexistent.key";
        String[] args = {"arg1", "arg2"};

        when(messageSource.getMessage(key, args, Locale.getDefault()))
                .thenThrow(new NoSuchMessageException(key, Locale.getDefault()));

        // When & Then
        assertThrows(NoSuchMessageException.class, () -> {
            messageService.getMessage(key, args);
        });

        verify(messageSource, times(1)).getMessage(key, args, Locale.getDefault());
    }

    @Test
    public void whenGetMessageWithNullKeyShouldThrowIllegalArgumentException() {
        // Given
        String key = null;
        String[] args = {"arg1"};

        when(messageSource.getMessage(key, args, Locale.getDefault()))
                .thenThrow(new IllegalArgumentException("Message key cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.getMessage(key, args);
        });

        verify(messageSource, times(1)).getMessage(key, args, Locale.getDefault());
    }

    @Test
    public void whenGetMessageWithEmptyKeyShouldHandleGracefully() {
        // Given
        String key = "";
        String expectedMessage = "Empty key message";

        when(messageSource.getMessage(key, new Object[0], Locale.getDefault()))
                .thenReturn(expectedMessage);

        // When
        String result = messageService.getMessage(key);

        // Then
        assertEquals(expectedMessage, result);
        verify(messageSource, times(1)).getMessage(key, new Object[0], Locale.getDefault());
    }

    @Test
    public void whenGetMessageShouldUseDefaultLocale() {
        // Given
        String key = "test.message";
        String expectedMessage = "Test message";
        Locale expectedLocale = Locale.getDefault();

        when(messageSource.getMessage(key, new Object[0], expectedLocale))
                .thenReturn(expectedMessage);

        // When
        messageService.getMessage(key);

        // Then
        verify(messageSource, times(1)).getMessage(key, new Object[0], expectedLocale);
    }
}
