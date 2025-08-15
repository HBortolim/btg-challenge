package com.btg.challenge.game;

import com.btg.challenge.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameServiceTest {
    @Mock
    private GameRepository gameRepository;

    @Spy
    private GameMapper gameMapper = Mappers.getMapper(GameMapper.class);

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenSaveGameShouldReturnSavedGameDto() {
        GameDto gameDto = new GameDto();
        gameDto.setName("God of War");

        Game game = new Game();
        game.setName("God of War");

        when(gameMapper.toEntity(any(GameDto.class))).thenReturn(game);
        when(gameRepository.save(any(Game.class))).thenReturn(game);
        when(gameMapper.toDto(any(Game.class))).thenReturn(gameDto);

        gameService.save(gameDto);

        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    public void whenFindByIdWithValidIdShouldReturnGameDto() {
        Game game = new Game();
        game.setId(1L);
        GameDto gameDto = new GameDto();

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gameMapper.toDto(any(Game.class))).thenReturn(gameDto);

        gameService.findById(1L);

        verify(gameRepository, times(1)).findById(1L);
    }

    @Test
    public void whenFindByIdWithNonExistentIdShouldThrowResourceNotFoundException() {
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gameService.findById(1L));
    }

    @Test
    public void whenFindAllShouldReturnPageOfGameDtos() {
        Page<Game> gamePage = new PageImpl<>(Collections.singletonList(new Game()));
        when(gameRepository.findAll(any(Pageable.class))).thenReturn(gamePage);

        gameService.findAll(Pageable.unpaged());

        verify(gameRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void whenUpdateWithValidDataShouldReturnUpdatedGameDto() {
        Game game = new Game();
        game.setId(1L);
        GameDto gameDto = new GameDto();
        gameDto.setName("God of War Ragnarok");

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        gameService.update(1L, gameDto);

        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    public void whenUpdateWithNonExistentIdShouldThrowResourceNotFoundException() {
        GameDto gameDto = new GameDto();
        gameDto.setName("God of War Ragnarok");

        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gameService.update(1L, gameDto));
    }

    @Test
    public void whenDeleteByIdShouldCallRepositoryDelete() {
        doNothing().when(gameRepository).deleteById(1L);
        gameService.deleteById(1L);
        verify(gameRepository, times(1)).deleteById(1L);
    }
}
