package com.btg.challenge.game;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class GameDataPopulationService {
    private static final Logger log = LoggerFactory.getLogger(GameDataPopulationService.class);

    @Value("${game-api.url}")
    private String gameApiUrl;

    private final GameRepository gameRepository;

    private final RestTemplate restTemplate;

    public GameDataPopulationService(GameRepository gameRepository, RestTemplate restTemplate) {
        this.gameRepository = gameRepository;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void populateGames() {
        if (gameRepository.count() > 0) {
            log.info("Games already exist in the database, skipping population.");
            return;
        }

        try {
            log.info("Fetching games from external API: {}", gameApiUrl);
            PlaystationGameDto[] gameDtos = restTemplate.getForObject(gameApiUrl, PlaystationGameDto[].class);

            if (gameDtos != null && gameDtos.length > 0) {
                List<Game> games = Arrays.stream(gameDtos)
                        .limit(20)
                        .map(this::mapToGame)
                        .filter(Objects::nonNull)
                        .toList();

                if (!games.isEmpty()) {
                    gameRepository.saveAll(games);
                    log.info("Successfully populated {} games from the external API.", games.size());
                }
            }
        } catch (Exception e) {
            log.error("Error fetching games from API: {}", e.getMessage());
        }
    }

    private Game mapToGame(PlaystationGameDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty() ||
            dto.getGenre() == null || dto.getGenre().isEmpty()) {
            return null;
        }

        Game game = new Game();
        game.setName(dto.getName());
        game.setGenre(String.join(", ", dto.getGenre()));
        return game;
    }
}
