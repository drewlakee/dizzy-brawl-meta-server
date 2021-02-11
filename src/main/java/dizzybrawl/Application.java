package dizzybrawl;

import dizzybrawl.verticles.PgDatabaseVerticle;
import dizzybrawl.verticles.VertxLauncherVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Application {

    private final VertxLauncherVerticle launcherVerticle;
    private final MicrometerMetricsOptions configuredMetrics;

    /**
     *  Inject it for correct lifecycle:
     *  Hibernate Entities Generation -> Post Postgres Initialization SQL scripts
     *
     *  Make possible to execute other SQL things after hibernate generation
     *  @see PgDatabaseVerticle#postPostgresInitialization()
     */
    private final SessionFactory sessionFactory;

    @Autowired
    public Application(VertxLauncherVerticle launcherVerticle,
                       SessionFactory sessionFactory,
                       MicrometerMetricsOptions configuredMetrics) {
        this.launcherVerticle = launcherVerticle;
        this.sessionFactory = sessionFactory;
        this.configuredMetrics = configuredMetrics;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    private void launchVertx() {
        VertxOptions options = new VertxOptions();
        options.setMetricsOptions(configuredMetrics);

        Vertx vertx = Vertx.vertx(options);
        vertx.deployVerticle(launcherVerticle);
    }
}
