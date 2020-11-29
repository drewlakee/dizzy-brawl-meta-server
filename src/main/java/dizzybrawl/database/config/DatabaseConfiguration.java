package dizzybrawl.database.config;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = "classpath:db.properties")
public class DatabaseConfiguration {

    private final Environment environment;

    @Autowired
    public DatabaseConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public PgPool getReactiveVertxPgPool() {
        PgConnectOptions connectionOptions = new PgConnectOptions()
                .setHost(environment.getProperty("db.host", "localhost"))
                .setPort(environment.getProperty("db.port", Integer.class, 5432))
                .setDatabase(environment.getProperty("db.name", "dizzybrawl"))
                .setUser(environment.getProperty("db.client.username", "postgres"))
                .setPassword(environment.getProperty("db.client.password", "1"));

        PoolOptions connectionPoolOptions = new PoolOptions()
                .setMaxSize(environment.getProperty("db.pool.size", Integer.class, 5));

        return PgPool.pool(Vertx.vertx(), connectionOptions, connectionPoolOptions);
    }
}
