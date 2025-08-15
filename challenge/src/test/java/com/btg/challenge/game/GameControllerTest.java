package com.btg.challenge.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.btg.challenge.shared.config.JwtTokenProvider;
import com.btg.challenge.shared.exception.GlobalExceptionHandler;
import com.btg.challenge.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(GameController.class)
@Import({GameController.class, GlobalExceptionHandler.class})
public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return mock(JwtTokenProvider.class);
        }

        @Bean
        public GameService gameService() {
            return mock(GameService.class);
        }
    }

    @Test
    @WithMockUser(username = "user")
    public void whenCreateGameWithValidDataShouldReturnCreatedResponse() throws Exception {
        GameDto gameDto = new GameDto();
        gameDto.setName("God of War");

        when(gameService.save(any(GameDto.class))).thenReturn(gameDto);

        mockMvc.perform(post("/games")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gameDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenGetGameByIdWithValidIdShouldReturnOkResponse() throws Exception {
        GameDto gameDto = new GameDto();
        gameDto.setId(1L);
        gameDto.setName("God of War");
        when(gameService.findById(1L)).thenReturn(gameDto);

        mockMvc.perform(get("/games/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenGetGameByIdWithNonExistentIdShouldReturnNotFound() throws Exception {
        when(gameService.findById(1L)).thenThrow(new ResourceNotFoundException("Game not found"));

        mockMvc.perform(get("/games/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenGetAllGamesShouldReturnOkResponse() throws Exception {
        mockMvc.perform(get("/games"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenUpdateGameWithValidDataShouldReturnOkResponse() throws Exception {
        GameDto gameDto = new GameDto();
        gameDto.setName("God of War Ragnarok");
        when(gameService.update(eq(1L), any(GameDto.class))).thenReturn(gameDto);

        mockMvc.perform(put("/games/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gameDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenUpdateGameWithNonExistentIdShouldReturnNotFound() throws Exception {
        GameDto gameDto = new GameDto();
        gameDto.setName("God of War Ragnarok");

        doThrow(new ResourceNotFoundException("Game not found")).when(gameService).update(eq(1L), any(GameDto.class));

        mockMvc.perform(put("/games/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gameDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenDeleteGameShouldReturnNoContentResponse() throws Exception {
        mockMvc.perform(delete("/games/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
