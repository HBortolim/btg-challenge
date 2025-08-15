package com.btg.challenge.game;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.btg.challenge.shared.exception.ResourceNotFoundException;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    public GameService(GameRepository gameRepository, GameMapper gameMapper) {
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
    }

    public GameDto save(GameDto gameDto) {
        Game game = gameMapper.toEntity(gameDto);
        return gameMapper.toDto(gameRepository.save(game));
    }

    public Page<GameDto> findAll(Pageable pageable) {
        return gameRepository.findAll(pageable)
                .map(gameMapper::toDto);
    }

    public GameDto findById(Long id) {
        return gameRepository.findById(id)
                .map(gameMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + id));
    }

    public void deleteById(Long id) {
        gameRepository.deleteById(id);
    }

    public GameDto update(Long id, GameDto gameDto) {
        Game existingGame = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + id));
        existingGame.setName(gameDto.getName());
        existingGame.setGenre(gameDto.getGenre());
        return gameMapper.toDto(gameRepository.save(existingGame));
    }
}
