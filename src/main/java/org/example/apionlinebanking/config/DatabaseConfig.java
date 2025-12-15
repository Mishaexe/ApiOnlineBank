package org.example.apionlinebanking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                // DATABASE_URL имеет вид: postgres://user:pass@host:port/dbname
                URI uri = new URI(databaseUrl);

                String username = uri.getUserInfo().split(":")[0];
                String password = uri.getUserInfo().split(":")[1];
                String host = uri.getHost();
                int port = uri.getPort();
                String path = uri.getPath(); // начинается с /

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;

                return DataSourceBuilder.create()
                        .driverClassName("org.postgresql.Driver")
                        .url(jdbcUrl)
                        .username(username)
                        .password(password)
                        .build();

            } catch (Exception e) {
                throw new RuntimeException("Не удалось подключиться к базе через DATABASE_URL", e);
            }
        } else {
            // Локальный режим (для разработки)
            return DataSourceBuilder.create()
                    .driverClassName("org.postgresql.Driver")
                    .url("jdbc:postgresql://localhost:5432/ApiOnlineBankingdb")
                    .username("postgres")
                    .password("postgres")
                    .build();
        }
    }
}
