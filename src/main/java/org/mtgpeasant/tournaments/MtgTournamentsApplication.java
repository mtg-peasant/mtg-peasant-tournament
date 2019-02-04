package org.mtgpeasant.tournaments;

import org.mtgpeasant.tournaments.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableConfigurationProperties(AppProperties.class)
@EnableSwagger2
public class MtgTournamentsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MtgTournamentsApplication.class, args);
    }
}
