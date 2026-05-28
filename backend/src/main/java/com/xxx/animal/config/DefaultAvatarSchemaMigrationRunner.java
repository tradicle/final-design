package com.xxx.animal.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class DefaultAvatarSchemaMigrationRunner implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    public DefaultAvatarSchemaMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS default_avatar (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
                    name VARCHAR(50) NOT NULL COMMENT 'Avatar Name',
                    image_data LONGTEXT NOT NULL COMMENT 'Avatar Image Data URI',
                    sort_order INT DEFAULT 0 COMMENT 'Sort Order',
                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Default Avatar Table'
                """);

        List<AvatarSeed> seeds = List.of(
                new AvatarSeed("\u5976\u6cb9\u732b\u54aa", svgDataUri("""
                        <svg xmlns="http://www.w3.org/2000/svg" width="160" height="160" viewBox="0 0 160 160">
                          <rect width="160" height="160" rx="40" fill="#FFE7D6"/>
                          <circle cx="80" cy="92" r="40" fill="#F7CBA9"/>
                          <path d="M48 64 L62 32 L78 64 Z" fill="#F7CBA9"/>
                          <path d="M82 64 L98 32 L112 64 Z" fill="#F7CBA9"/>
                          <path d="M58 57 L63 45 L70 58 Z" fill="#F5A5A5"/>
                          <path d="M90 58 L97 45 L102 57 Z" fill="#F5A5A5"/>
                          <ellipse cx="66" cy="92" rx="6" ry="8" fill="#2F2926"/>
                          <ellipse cx="94" cy="92" rx="6" ry="8" fill="#2F2926"/>
                          <path d="M80 104 C75 110 85 110 80 104 Z" fill="#D97757"/>
                          <path d="M72 113 C76 118 84 118 88 113" fill="none" stroke="#2F2926" stroke-width="4" stroke-linecap="round"/>
                          <path d="M48 102 C58 98 64 98 72 101" fill="none" stroke="#8F6A56" stroke-width="3" stroke-linecap="round"/>
                          <path d="M88 101 C96 98 102 98 112 102" fill="none" stroke="#8F6A56" stroke-width="3" stroke-linecap="round"/>
                        </svg>
                        """), 1),
                new AvatarSeed("\u7070\u767d\u732b\u54aa", svgDataUri("""
                        <svg xmlns="http://www.w3.org/2000/svg" width="160" height="160" viewBox="0 0 160 160">
                          <rect width="160" height="160" rx="40" fill="#E9EEF5"/>
                          <circle cx="80" cy="92" r="40" fill="#F5F8FB"/>
                          <path d="M48 64 L63 30 L81 64 Z" fill="#C6D0DC"/>
                          <path d="M79 64 L97 30 L112 64 Z" fill="#C6D0DC"/>
                          <path d="M60 57 L66 44 L72 58 Z" fill="#F7BBC1"/>
                          <path d="M89 58 L95 44 L100 57 Z" fill="#F7BBC1"/>
                          <ellipse cx="66" cy="92" rx="6" ry="8" fill="#31353A"/>
                          <ellipse cx="94" cy="92" rx="6" ry="8" fill="#31353A"/>
                          <path d="M80 103 C74 109 86 109 80 103 Z" fill="#F08A8A"/>
                          <path d="M71 114 C76 120 84 120 89 114" fill="none" stroke="#31353A" stroke-width="4" stroke-linecap="round"/>
                          <path d="M56 74 C63 68 71 66 80 67" fill="none" stroke="#9DA8B6" stroke-width="6" stroke-linecap="round"/>
                          <path d="M80 67 C89 66 97 68 104 74" fill="none" stroke="#9DA8B6" stroke-width="6" stroke-linecap="round"/>
                        </svg>
                        """), 2),
                new AvatarSeed("\u67ef\u57fa\u72d7\u72d7", svgDataUri("""
                        <svg xmlns="http://www.w3.org/2000/svg" width="160" height="160" viewBox="0 0 160 160">
                          <rect width="160" height="160" rx="40" fill="#FFF1D9"/>
                          <circle cx="80" cy="92" r="40" fill="#F6B36A"/>
                          <path d="M42 70 C36 42 54 32 70 54 L67 77 Z" fill="#C97B3E"/>
                          <path d="M118 70 C124 42 106 32 90 54 L93 77 Z" fill="#C97B3E"/>
                          <path d="M54 72 C49 54 59 48 69 60 L67 76 Z" fill="#FFF7F1"/>
                          <path d="M106 72 C111 54 101 48 91 60 L93 76 Z" fill="#FFF7F1"/>
                          <ellipse cx="66" cy="91" rx="6" ry="8" fill="#2E2924"/>
                          <ellipse cx="94" cy="91" rx="6" ry="8" fill="#2E2924"/>
                          <ellipse cx="80" cy="106" rx="11" ry="9" fill="#2E2924"/>
                          <path d="M69 116 C74 123 86 123 91 116" fill="none" stroke="#2E2924" stroke-width="4" stroke-linecap="round"/>
                          <path d="M54 96 C62 103 98 103 106 96" fill="none" stroke="#FFF7F1" stroke-width="10" stroke-linecap="round"/>
                        </svg>
                        """), 3),
                new AvatarSeed("\u767d\u8272\u72d7\u72d7", svgDataUri("""
                        <svg xmlns="http://www.w3.org/2000/svg" width="160" height="160" viewBox="0 0 160 160">
                          <rect width="160" height="160" rx="40" fill="#E9F6FF"/>
                          <circle cx="80" cy="92" r="40" fill="#FFFFFF"/>
                          <ellipse cx="48" cy="80" rx="14" ry="26" fill="#D8ECF8" transform="rotate(-18 48 80)"/>
                          <ellipse cx="112" cy="80" rx="14" ry="26" fill="#D8ECF8" transform="rotate(18 112 80)"/>
                          <ellipse cx="66" cy="92" rx="6" ry="8" fill="#34312F"/>
                          <ellipse cx="94" cy="92" rx="6" ry="8" fill="#34312F"/>
                          <ellipse cx="80" cy="106" rx="10" ry="8" fill="#34312F"/>
                          <path d="M71 115 C75 121 85 121 89 115" fill="none" stroke="#34312F" stroke-width="4" stroke-linecap="round"/>
                          <circle cx="54" cy="70" r="6" fill="#F7C9D9"/>
                          <circle cx="106" cy="70" r="6" fill="#F7C9D9"/>
                        </svg>
                        """), 4)
        );

        for (AvatarSeed seed : seeds) {
            Integer exists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM default_avatar WHERE sort_order = ?",
                    Integer.class,
                    seed.sortOrder()
            );

            if (exists != null && exists > 0) {
                jdbcTemplate.update(
                        "UPDATE default_avatar SET name = ? WHERE sort_order = ?",
                        seed.name(),
                        seed.sortOrder()
                );
                continue;
            }

            jdbcTemplate.update("""
                    INSERT INTO default_avatar (name, image_data, sort_order)
                    VALUES (?, ?, ?)
                    """, seed.name(), seed.imageData(), seed.sortOrder());
        }
    }

    private String svgDataUri(String svg) {
        return "data:image/svg+xml;utf8," + URLEncoder.encode(svg, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private record AvatarSeed(String name, String imageData, int sortOrder) {
    }
}
