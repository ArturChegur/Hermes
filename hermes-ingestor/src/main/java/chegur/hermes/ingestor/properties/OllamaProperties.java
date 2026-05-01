package chegur.hermes.ingestor.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ollama")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OllamaProperties {

  String baseUrl;

  String model;
}