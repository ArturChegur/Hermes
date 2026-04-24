package chegur.hermes.ingestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HermesIngestorApplication {

  public static void main(String[] args) {
    SpringApplication.run(HermesIngestorApplication.class, args);
  }
}