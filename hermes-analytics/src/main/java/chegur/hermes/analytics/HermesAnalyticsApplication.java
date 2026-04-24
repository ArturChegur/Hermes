package chegur.hermes.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HermesAnalyticsApplication {

  public static void main(String[] args) {
    SpringApplication.run(HermesAnalyticsApplication.class, args);
  }
}
