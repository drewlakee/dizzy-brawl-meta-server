package dizzybrawl.metrics;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MetricsConfiguration {

    private final Logger log = LoggerFactory.getLogger(MetricsConfiguration.class);

    private final Environment env;

    @Autowired
    public MetricsConfiguration(Environment env) {
        this.env = env;
    }

    @Bean
    public MicrometerMetricsOptions configurePrometheusVertxAndJvmMetrics() {
        MicrometerMetricsOptions options = new MicrometerMetricsOptions().setEnabled(true);
        VertxPrometheusOptions vertxPrometheusOptions = new VertxPrometheusOptions()
                .setEmbeddedServerOptions(new HttpServerOptions().setPort(env.getProperty("vertx.prometheus.endpoint.port", Integer.class, 8081)))
                .setEmbeddedServerEndpoint("/metrics")
                .setEnabled(true)
                .setStartEmbeddedServer(true);

        log.info("Vertx Metrics port is '{}' with endpoint '{}'",
                env.getProperty("vertx.prometheus.endpoint.port", Integer.class, 8081),
                vertxPrometheusOptions.getEmbeddedServerEndpoint());

        options.setPrometheusOptions(vertxPrometheusOptions);
        options.setJvmMetricsEnabled(true);

        return options;
    }
}
