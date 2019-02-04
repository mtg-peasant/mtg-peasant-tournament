package org.mtgpeasant.tournaments.config;

import org.mtgpeasant.tournaments.thymeleaf.MtgTournamentsDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebMvcConfig {
    /**
     * Add mtg-tournaments dialect to thymeleaf engine
     */
    @Bean
    public MtgTournamentsDialect mtgTournamentsDialect() {
        return new MtgTournamentsDialect();
    }
}
