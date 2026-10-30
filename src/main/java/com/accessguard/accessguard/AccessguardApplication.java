package com.accessguard.accessguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class AccessguardApplication {

	public static void main(String[] args) {
		configureRenderDatabase();
		SpringApplication.run(AccessguardApplication.class, args);
	}

	private static void configureRenderDatabase() {
		String databaseUrl = System.getenv("DATABASE_URL");
		// If Render provides standard DATABASE_URL (e.g. postgres://user:pass@host:port/db)
		// and Spring datasource is not manually set, convert it to JDBC format
		if (databaseUrl != null && !databaseUrl.isBlank()
				&& System.getProperty("spring.datasource.url") == null
				&& System.getenv("SPRING_DATASOURCE_URL") == null) {
			try {
				URI uri = new URI(databaseUrl);
				String userInfo = uri.getUserInfo();
				if (userInfo != null) {
					String[] parts = userInfo.split(":", 2);
					String username = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
					System.setProperty("spring.datasource.username", username);
					if (parts.length > 1) {
						String password = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
						System.setProperty("spring.datasource.password", password);
					}
				}
				int port = uri.getPort() == -1 ? 5432 : uri.getPort();
				String query = uri.getQuery() != null ? "?" + uri.getQuery() : "";
				String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath() + query;
				System.setProperty("spring.datasource.url", jdbcUrl);
			} catch (Exception e) {
				System.err.println("Note: Could not parse DATABASE_URL: " + e.getMessage());
			}
		}
	}

}
