package org.erplite.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Runs Flyway migrations on application startup.
 *
 * Migrations are loaded from classpath:db/migration/ folder.
 * Files follow naming convention: V1__description.sql, V2__description.sql, etc.
 */
@Component
public class FlywayInitializer {

	@Autowired
	private DataSource dataSource; // provided by Next Framework

	@PostConstruct
	public void migrate() {
		Flyway flyway = Flyway.configure()
				.dataSource(dataSource)
				.locations("classpath:db/migration")
				.load();
		flyway.migrate();
	}

}
