package chegur.hermes.analytics.properties;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class KafkaAnalyticsPropertiesTest {

  @Test
  void shouldExposeConfiguredValuesViaConstructor() {
    KafkaAnalyticsProperties properties = new KafkaAnalyticsProperties("telegram-updates", "hermes-analytics", "telegram-updates-dlt");

    assertThat(properties.getUpdatesTopic()).isEqualTo("telegram-updates");
    assertThat(properties.getConsumerGroupId()).isEqualTo("hermes-analytics");
    assertThat(properties.getDltTopic()).isEqualTo("telegram-updates-dlt");
  }
}
