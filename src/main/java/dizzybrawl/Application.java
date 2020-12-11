package dizzybrawl;

import dizzybrawl.verticles.PgDatabaseVerticle;
import dizzybrawl.verticles.VertxLauncherVerticle;
import io.vertx.core.Vertx;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Application {

    public static ConfigurableApplicationContext configurableApplicationContext;

    private final VertxLauncherVerticle launcherVerticle;

    /**
     *  Inject it for correct lifecycle:
     *  Hibernate Entities Generation -> Custom SQL injections
     *
     *  Make possible to execute other SQL things after hibernate mapping
     *  @see PgDatabaseVerticle#buildSqlTriggers()
     */
    private final SessionFactory sessionFactory;

    @Autowired
    public Application(VertxLauncherVerticle launcherVerticle,
                       SessionFactory sessionFactory) {
        this.launcherVerticle = launcherVerticle;
        this.sessionFactory = sessionFactory;
    }

    @PostConstruct
    private void launchVertx() {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(launcherVerticle, ar1 -> {
            if (ar1.failed()) {
                SpringApplication.exit(configurableApplicationContext, () -> 0);
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        Application.configurableApplicationContext = SpringApplication.run(Application.class, args);
    }
}
