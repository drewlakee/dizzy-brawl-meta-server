package dizzybrawl.database.config;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
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

    private final Environment environment;

    @Autowired
    public DatabaseConfiguration(Environment environment) {
        this.environment = environment;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.auto"));
        return properties;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getProperty("database.jdbc.driverClassName"));
        dataSource.setUrl(environment.getProperty("database.url"));
        dataSource.setUsername(environment.getProperty("database.username"));
        dataSource.setPassword(environment.getProperty("database.password"));
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
                .setHost(environment.getProperty("database.host"))
                .setDatabase(environment.getProperty("database.name"))
                .setUser(environment.getProperty("database.username"))
                .setPassword(environment.getProperty("database.password"));

        // maybe build at docker then port is not mandatory
        if (environment.containsProperty("database.port") && !environment.getProperty("database.port").equals("none")) {
            connectionOptions.setPort(environment.getProperty("database.port", Integer.class));
        }

        PoolOptions connectionPoolOptions = new PoolOptions();

        if (environment.containsProperty("database.connection.pool.count")
                && environment.getProperty("database.connection.pool.count", Integer.class) > 0) {
            connectionPoolOptions.setMaxSize(environment.getProperty("database.connection.pool.count", Integer.class));
        } else {
            connectionPoolOptions.setMaxSize(1);
        }

        return PgPool.pool(Vertx.vertx(), connectionOptions, connectionPoolOptions);
    }
}
