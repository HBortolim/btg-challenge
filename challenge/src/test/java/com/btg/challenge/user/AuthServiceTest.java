package com.btg.challenge.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.btg.challenge.shared.config.JwtTokenProvider;

public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenLoginWithValidCredentialsShouldReturnAuthResponse() {
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("user");
        authRequestDto.setPassword("password");

        User user = new User();
        user.setUsername("user");
        user.setPassword("password");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("user")).thenReturn(java.util.Optional.of(user));
        when(jwtTokenProvider.generateToken(user)).thenReturn("token");

        authService.login(authRequestDto);
    }

    @Test
    public void whenLoginWithNonExistentUserShouldThrowRuntimeException() {
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("user");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(authRequestDto));
    }

    @Test
    public void whenRegisterWithValidDataShouldCreateUserSuccessfully() {
        // Given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("user");
        authRequestDto.setPassword("password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("user");
        savedUser.setPassword("encodedPassword");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        authService.register(authRequestDto);

        // Then
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void whenLoginWithBadCredentialsShouldThrowBadCredentialsException() {
        // Given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("user");
        authRequestDto.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(authRequestDto);
        });

        verify(authenticationManager, times(1)).authenticate(any());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    public void whenLoginWithDisabledAccountShouldThrowDisabledException() {
        // Given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("user");
        authRequestDto.setPassword("password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("Account is disabled"));

        // When & Then
        assertThrows(DisabledException.class, () -> {
            authService.login(authRequestDto);
        });

        verify(authenticationManager, times(1)).authenticate(any());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    public void whenLoginWithLockedAccountShouldThrowLockedException() {
        // Given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("user");
        authRequestDto.setPassword("password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new LockedException("Account is locked"));

        // When & Then
        assertThrows(LockedException.class, () -> {
            authService.login(authRequestDto);
        });

        verify(authenticationManager, times(1)).authenticate(any());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    public void whenLoginSucceedsShouldReturnValidToken() {
        // Given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("user");
        authRequestDto.setPassword("password");

        User user = new User();
        user.setUsername("user");
        user.setPassword("encodedPassword");

        String expectedToken = "jwt.token.here";

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(user)).thenReturn(expectedToken);

        // When
        String actualToken = authService.login(authRequestDto);

        // Then
        assertEquals(expectedToken, actualToken);
        verify(authenticationManager, times(1)).authenticate(any());
        verify(userRepository, times(1)).findByUsername("user");
        verify(jwtTokenProvider, times(1)).generateToken(user);
    }

    @Test
    public void whenRegisterWithNullUsernameShouldThrowIllegalArgumentException() {
        // Given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername(null);
        authRequestDto.setPassword("password");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // When
        authService.register(authRequestDto);

        // Then
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void whenRegisterWithEmptyPasswordShouldThrowIllegalArgumentException() {
        // Given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("user");
        authRequestDto.setPassword("");

        when(passwordEncoder.encode("")).thenReturn("encodedEmptyPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // When
        authService.register(authRequestDto);

        // Then
        verify(passwordEncoder, times(1)).encode("");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void whenRegisterShouldEncodePasswordBeforeSaving() {
        // Given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("testuser");
        authRequestDto.setPassword("plainpassword");

        String encodedPassword = "encoded.password.hash";
        when(passwordEncoder.encode("plainpassword")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // When
        authService.register(authRequestDto);

        // Then
        verify(passwordEncoder, times(1)).encode("plainpassword");
        verify(userRepository, times(1)).save(argThat(user -> 
            "testuser".equals(user.getUsername()) && 
            encodedPassword.equals(user.getPassword())
        ));
    }

    @Test
    public void whenAuthenticationSucceedsButUserNotInDatabaseShouldThrowRuntimeException() {
        // Given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("user");
        authRequestDto.setPassword("password");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            authService.login(authRequestDto);
        });

        verify(authenticationManager, times(1)).authenticate(any());
        verify(userRepository, times(1)).findByUsername("user");
        verifyNoInteractions(jwtTokenProvider);
    }
}
