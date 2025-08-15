package com.btg.challenge.shared.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.btg.challenge.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private UserDetails userDetails;
    private final String testSecret = "thisIsAVeryLongSecretKeyForTestingPurposesOnly1234567890";
    private final long testExpiration = 3600; // 1 hour in seconds

    @BeforeEach
    public void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", testExpiration);

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        userDetails = user;
    }

    @Test
    public void whenGenerateTokenShouldReturnValidJwtToken() {
        // When
        String token = jwtTokenProvider.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }

    @Test
    public void whenExtractUsernameFromValidTokenShouldReturnCorrectUsername() {
        // Given
        String token = jwtTokenProvider.generateToken(userDetails);

        // When
        String extractedUsername = jwtTokenProvider.extractUsername(token);

        // Then
        assertEquals("testuser", extractedUsername);
    }

    @Test
    public void whenExtractExpirationFromValidTokenShouldReturnFutureDate() {
        // Given
        String token = jwtTokenProvider.generateToken(userDetails);
        Date beforeGeneration = new Date();

        // When
        Date expiration = jwtTokenProvider.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(beforeGeneration));
        assertTrue(expiration.before(new Date(System.currentTimeMillis() + (testExpiration + 10) * 1000)));
    }

    @Test
    public void whenValidateTokenWithCorrectUserShouldReturnTrue() {
        // Given
        String token = jwtTokenProvider.generateToken(userDetails);

        // When
        Boolean isValid = jwtTokenProvider.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    public void whenValidateTokenWithWrongUserShouldReturnFalse() {
        // Given
        String token = jwtTokenProvider.generateToken(userDetails);
        User differentUser = new User();
        differentUser.setUsername("differentuser");
        differentUser.setPassword("password");

        // When
        Boolean isValid = jwtTokenProvider.validateToken(token, differentUser);

        // Then
        assertFalse(isValid);
    }

    @Test
    public void whenValidateExpiredTokenShouldReturnFalseOrThrowException() {
        // Given
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", -10);
        String expiredToken = jwtTokenProvider.generateToken(userDetails);
        
        // Reset expiration
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", testExpiration);

        // When & Then
        try {
            Boolean isValid = jwtTokenProvider.validateToken(expiredToken, userDetails);
            assertFalse(isValid);
        } catch (Exception e) {
            assertTrue(e instanceof io.jsonwebtoken.ExpiredJwtException);
        }
    }

    @Test
    public void whenExtractClaimFromValidTokenShouldReturnCorrectValues() {
        // Given
        String token = jwtTokenProvider.generateToken(userDetails);

        // When
        String subject = jwtTokenProvider.extractClaim(token, Claims::getSubject);
        Date issuedAt = jwtTokenProvider.extractClaim(token, Claims::getIssuedAt);

        // Then
        assertEquals("testuser", subject);
        assertNotNull(issuedAt);
        assertTrue(issuedAt.before(new Date()));
    }

    @Test
    public void whenExtractUsernameFromMalformedTokenShouldThrowMalformedJwtException() {
        // Given
        String malformedToken = "invalid.jwt.token";

        // When & Then
        assertThrows(MalformedJwtException.class, () -> {
            jwtTokenProvider.extractUsername(malformedToken);
        });
    }

    @Test
    public void whenExtractUsernameFromTokenWithInvalidSignatureShouldThrowSignatureException() {
        // Given
        String wrongSecret = "different-secret-key-for-testing-purposes-that-is-256-bits";
        String tokenWithWrongSignature = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + testExpiration * 1000))
                .signWith(Keys.hmacShaKeyFor(wrongSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        // When & Then
        assertThrows(SignatureException.class, () -> {
            jwtTokenProvider.extractUsername(tokenWithWrongSignature);
        });
    }

    @Test
    public void whenExtractExpirationFromExpiredTokenShouldThrowExpiredJwtException() {
        // Given
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", -3600);
        String expiredToken = jwtTokenProvider.generateToken(userDetails);
        
        // Reset expiration
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", testExpiration);

        // When & Then
        assertThrows(ExpiredJwtException.class, () -> {
            jwtTokenProvider.extractExpiration(expiredToken);
        });
    }

    @Test
    public void whenValidateNullTokenShouldThrowIllegalArgumentException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtTokenProvider.validateToken(null, userDetails);
        });
    }

    @Test
    public void whenValidateEmptyTokenShouldThrowIllegalArgumentException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtTokenProvider.validateToken("", userDetails);
        });
    }
}
