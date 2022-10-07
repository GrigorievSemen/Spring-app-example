package ru.grigoriev.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("ru.grigoriev.entity")
@EnableJpaRepositories(basePackages = {"ru.grigoriev.repository"})
@ComponentScan({"ru.grigoriev.repository"})
public class SystemTestingJpaConfig {
}
