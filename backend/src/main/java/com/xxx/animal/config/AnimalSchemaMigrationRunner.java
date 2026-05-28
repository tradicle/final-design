package com.xxx.animal.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AnimalSchemaMigrationRunner implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    public AnimalSchemaMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        Integer locationColumnCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'animal'
                  AND column_name = 'location'
                """, Integer.class);

        if (locationColumnCount == null || locationColumnCount == 0) {
            jdbcTemplate.execute("""
                    ALTER TABLE animal
                    ADD COLUMN location VARCHAR(255) NULL COMMENT 'Found Location Name' AFTER detail_content
                    """);
        }
    }
}
