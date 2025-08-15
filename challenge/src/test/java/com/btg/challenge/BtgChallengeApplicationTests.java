package com.btg.challenge;

import com.btg.challenge.game.GameDataPopulationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@SpringBootTest
class BtgChallengeApplicationTests {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public GameDataPopulationService gameDataPopulationService() {
            return mock(GameDataPopulationService.class);
        }
    }

    @Test
    void contextLoads() {
    }

}
