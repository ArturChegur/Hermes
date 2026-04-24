package chegur.hermes.analytics.configuration;

import chegur.hermes.analytics.dto.TelegramMessageEvent;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {

  @Bean
  public ConsumerFactory<String, TelegramMessageEvent> telegramMessageConsumerFactory(KafkaProperties kafkaProperties) {
    Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());

    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

    props.put(JsonDeserializer.TRUSTED_PACKAGES, TelegramMessageEvent.class.getPackageName());
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TelegramMessageEvent.class.getName());
    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TelegramMessageEvent> telegramKafkaListenerContainerFactory(ConsumerFactory<String, TelegramMessageEvent> telegramMessageConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, TelegramMessageEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(telegramMessageConsumerFactory);
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

    DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(3000L, FixedBackOff.UNLIMITED_ATTEMPTS));

    factory.setCommonErrorHandler(errorHandler);

    return factory;
  }
}