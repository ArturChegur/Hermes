package chegur.hermes.analytics.configuration;

import chegur.hermes.analytics.dto.TelegramMessageEvent;
import chegur.hermes.analytics.properties.KafkaAnalyticsProperties;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {

  @Bean
  public ConsumerFactory<String, TelegramMessageEvent> telegramMessageConsumerFactory(KafkaProperties kafkaProperties) {
    Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());

    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

    props.put(JsonDeserializer.TRUSTED_PACKAGES, TelegramMessageEvent.class.getPackageName());
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TelegramMessageEvent.class.getName());
    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TelegramMessageEvent> telegramKafkaListenerContainerFactory(ConsumerFactory<String, TelegramMessageEvent> telegramMessageConsumerFactory, DefaultErrorHandler telegramKafkaErrorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, TelegramMessageEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(telegramMessageConsumerFactory);
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    factory.setCommonErrorHandler(telegramKafkaErrorHandler);

    return factory;
  }

  @Bean
  public DefaultErrorHandler telegramKafkaErrorHandler(ConsumerRecordRecoverer telegramDltRecoverer) {
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(telegramDltRecoverer, new FixedBackOff(3000L, 3L));
    errorHandler.setCommitRecovered(true);

    return errorHandler;
  }

  @Bean
  public ConsumerRecordRecoverer telegramDltRecoverer(KafkaTemplate<String, TelegramMessageEvent> kafkaTemplate, KafkaAnalyticsProperties kafkaAnalyticsProperties) {
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate, (ConsumerRecord<?, ?> record, Exception ex) -> new TopicPartition(kafkaAnalyticsProperties.getDltTopic(), record.partition()));

    return (record, exception) -> {
      log.error("Publishing poisoned kafka record to DLQ. topic = {}, partition = {}, offset = {}, dltTopic = {}",
        record.topic(), record.partition(), record.offset(), kafkaAnalyticsProperties.getDltTopic(), exception);

      recoverer.accept(record, exception);
    };
  }
}