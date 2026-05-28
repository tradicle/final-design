package com.xxx.animal.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserSchemaMigrationRunner implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    public UserSchemaMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        Integer nicknameColumnCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'sys_user'
                  AND column_name = 'nickname'
                """, Integer.class);
        if (nicknameColumnCount == null || nicknameColumnCount == 0) {
            jdbcTemplate.execute("""
                    ALTER TABLE sys_user
                    ADD COLUMN nickname VARCHAR(50) NULL COMMENT 'Nickname' AFTER username
                    """);
        }

        String avatarDataType = jdbcTemplate.queryForObject("""
                SELECT DATA_TYPE
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'sys_user'
                  AND column_name = 'avatar'
                """, String.class);
        if (avatarDataType != null && !"longtext".equalsIgnoreCase(avatarDataType)) {
            jdbcTemplate.execute("""
                    ALTER TABLE sys_user
                    MODIFY COLUMN avatar LONGTEXT NULL COMMENT 'User Avatar'
                    """);
        }

        jdbcTemplate.execute("""
                UPDATE sys_user
                SET nickname = username
                WHERE nickname IS NULL OR nickname = ''
                """);
    }
}
