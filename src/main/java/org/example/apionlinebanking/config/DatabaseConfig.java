package org.example.apionlinebanking.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        log.info("DATABASE_URL is present: {}", databaseUrl != null);

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                URI uri = new URI(databaseUrl);
                String[] userPass = uri.getUserInfo().split(":");
                String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + uri.getPort() + uri.getPath() + "?sslmode=require";

                log.info("Using JDBC URL: jdbc:postgresql://{}:{}{}", uri.getHost(), uri.getPort(), uri.getPath());

                return DataSourceBuilder.create()
                        .driverClassName("org.postgresql.Driver")
                        .url(jdbcUrl)
                        .username(userPass[0])
                        .password(userPass[1])
                        .build();
            } catch (Exception e) {
                log.error("Failed to parse DATABASE_URL", e);
                throw new RuntimeException(e);
            }
        } else {
            log.warn("No DATABASE_URL found — using local fallback");
            return DataSourceBuilder.create()
                    .driverClassName("org.postgresql.Driver")
                    .url("jdbc:postgresql://localhost:5432/ApiOnlineBankingdb")
                    .username("postgres")
                    .password("postgres")
                    .build();
        }
    }
}
