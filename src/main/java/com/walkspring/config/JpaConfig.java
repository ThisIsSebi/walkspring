package com.walkspring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    public JpaConfig() {
        System.out.println("Sout: JPA Auditing ist aktiv.");
    }
}
