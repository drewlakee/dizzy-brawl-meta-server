package dizzybrawl.database.config;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class DatabaseConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private final Environment environment;

    @Autowired
    public DatabaseConfiguration(Environment environment) {
        this.environment = environment;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL82Dialect"));
        properties.put("hibernate.show_sql", environment.getProperty("hibernate.show_sql", "false"));
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto", "create"));
        return properties;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getProperty("database.jdbc.driverClassName", "org.postgresql.Driver"));
        dataSource.setUrl(environment.getProperty("database.url", "jdbc:postgresql://postgres/dizzy-brawl"));
        dataSource.setUsername(environment.getProperty("database.username", "dizzy-brawl"));
        dataSource.setPassword(environment.getProperty("database.password", "dizzy-brawl"));
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("dizzybrawl.database.models");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    public PgPool getReactiveVertxPgPool() {
        PgConnectOptions connectionOptions = new PgConnectOptions()
                .setHost(environment.getProperty("database.host", "postgres"))
                .setDatabase(environment.getProperty("database.name", "dizzy-brawl"))
                .setUser(environment.getProperty("database.username", "dizzy-brawl"))
                .setPassword(environment.getProperty("database.password", "dizzy-brawl"));

        if (environment.containsProperty("database.port")) {
            connectionOptions.setPort(environment.getRequiredProperty("database.port", Integer.class));
        }

        PoolOptions connectionPoolOptions = new PoolOptions();
        connectionPoolOptions.setMaxSize(environment.getProperty("database.connection.pool.count", Integer.class, 1));
        log.info("Database connections pool size: {}", environment.getProperty("database.connection.pool.count", Integer.class, 1));

        return PgPool.pool(Vertx.vertx(), connectionOptions, connectionPoolOptions);
    }
}
