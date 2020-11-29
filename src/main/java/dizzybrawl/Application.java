package dizzybrawl;

import dizzybrawl.verticles.VertxLauncherVerticle;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Application {

    private final VertxLauncherVerticle launcherVerticle;

    @Autowired
    public Application(VertxLauncherVerticle launcherVerticle) {
        this.launcherVerticle = launcherVerticle;
    }

    @PostConstruct
    private void launchVertx() {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(launcherVerticle);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
