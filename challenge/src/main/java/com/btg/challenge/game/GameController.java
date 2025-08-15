package com.btg.challenge.game;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody GameDto gameDto) {
        return ResponseEntity.status(201).body(gameService.save(gameDto));
    }

    @GetMapping
    public ResponseEntity<Page<GameDto>> getAllGames(Pageable pageable) {
        return ResponseEntity.ok(gameService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getGameById(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameDto> updateGame(@PathVariable Long id, @RequestBody GameDto gameDto) {
        return ResponseEntity.ok(gameService.update(id, gameDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
