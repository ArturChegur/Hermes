package chegur.hermes.analytics.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hermes.kafka")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KafkaAnalyticsProperties {

  String updatesTopic;

  String consumerGroupId;

  String dltTopic;
}