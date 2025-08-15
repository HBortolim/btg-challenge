package com.btg.challenge.user;

import com.btg.challenge.shared.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenLoadUserByUsernameWithValidUsernameShouldReturnUserDetails() {
        // Given
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("password", result.getPassword());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        assertTrue(result.isEnabled());
        verify(userRepository, times(1)).findByUsername(username);
        verifyNoInteractions(messageService);
    }

    @Test
    public void whenLoadUserByUsernameWithNonExistentUserShouldThrowUsernameNotFoundException() {
        // Given
        String username = "nonexistentuser";
        String errorMessage = "User not found: " + username;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(messageService.getMessage("user.not.found", username)).thenReturn(errorMessage);

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username)
        );

        assertEquals(errorMessage, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(messageService, times(1)).getMessage("user.not.found", username);
    }

    @Test
    public void whenLoadUserByUsernameWithEmptyUsernameShouldThrowUsernameNotFoundException() {
        // Given
        String username = "";
        String errorMessage = "User not found: ";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(messageService.getMessage("user.not.found", username)).thenReturn(errorMessage);

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username)
        );

        assertEquals(errorMessage, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(messageService, times(1)).getMessage("user.not.found", username);
    }

    @Test
    public void whenLoadUserByUsernameWithNullUsernameShouldThrowUsernameNotFoundException() {
        // Given
        String username = null;
        String errorMessage = "User not found: null";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(messageService.getMessage("user.not.found", username)).thenReturn(errorMessage);

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username)
        );

        assertEquals(errorMessage, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(messageService, times(1)).getMessage("user.not.found", username);
    }
}
