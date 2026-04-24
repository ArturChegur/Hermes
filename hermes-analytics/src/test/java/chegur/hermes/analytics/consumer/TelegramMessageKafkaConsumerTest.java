package chegur.hermes.analytics.consumer;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import chegur.hermes.analytics.dto.TelegramMessageEvent;
import chegur.hermes.analytics.properties.KafkaAnalyticsProperties;
import chegur.hermes.analytics.service.TelegramMessagePersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
class TelegramMessageKafkaConsumerTest {

  @Mock
  private TelegramMessagePersistenceService persistenceService;

  @Mock
  private Acknowledgment acknowledgment;

  @Mock
  private KafkaAnalyticsProperties kafkaAnalyticsProperties;

  @InjectMocks
  private TelegramMessageKafkaConsumer consumer;

  @Test
  void consumeShouldPersistAndAcknowledgeEvent() {
    TelegramMessageEvent event = new TelegramMessageEvent();
    event.setUpdateId(42L);
    event.setMessageId(7);

    consumer.consume(event, acknowledgment);

    verify(persistenceService).persist(event);
    verify(acknowledgment).acknowledge();
  }

  @Test
  void consumeShouldAcknowledgeAndSkipPersistenceWhenEventIsNull() {
    consumer.consume(null, acknowledgment);

    verify(persistenceService, never()).persist(org.mockito.ArgumentMatchers.any());
    verify(acknowledgment).acknowledge();
  }
}
