package com.btg.challenge.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.btg.challenge.shared.config.JwtTokenProvider;
import com.btg.challenge.shared.exception.GlobalExceptionHandler;
import com.btg.challenge.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@Import({AuthController.class, GlobalExceptionHandler.class})
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(username = "user")
    public void whenLoginWithValidCredentialsShouldReturnSuccessResponse() throws Exception {
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("user");
        authRequestDto.setPassword("password");

        when(authService.login(any(AuthRequestDto.class))).thenReturn("token");

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenLoginWithNonExistentUserShouldReturnNotFound() throws Exception {
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("user");
        authRequestDto.setPassword("password");

        when(authService.login(any(AuthRequestDto.class))).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenRegisterWithValidDataShouldReturnSuccessResponse() throws Exception {
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setUsername("user");
        authRequestDto.setPassword("password");

        doNothing().when(authService).register(any(AuthRequestDto.class));

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDto)))
                .andExpect(status().isCreated());
    }
}
