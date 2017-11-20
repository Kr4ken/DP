package com.kr4ken.dp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.HashMap;

/**
 * Класс для получения конфигурационных данных по Divine
 */
@Configuration
@PropertySource("classpath:divine.properties")
public class DivineConfig {

    @Value("${divine.trelloSync}")
    private Boolean trelloSync;

    @Value("${divine.habiticaSync}")
    private Boolean habiticaSync;

    public DivineConfig() {
    }

    public Boolean getTrelloSync() {
        return trelloSync;
    }

    public Boolean getHabiticaSync() {
        return habiticaSync;
    }
}
