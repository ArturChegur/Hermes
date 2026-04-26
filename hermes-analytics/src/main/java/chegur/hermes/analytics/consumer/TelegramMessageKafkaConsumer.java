package chegur.hermes.analytics.consumer;

import chegur.hermes.analytics.dto.TelegramMessageEvent;
import chegur.hermes.analytics.service.TelegramMessagePersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramMessageKafkaConsumer {

  private final TelegramMessagePersistenceService persistenceService;

  @KafkaListener(
    topics = "${hermes.kafka.updates-topic}",
    groupId = "${hermes.kafka.consumer-group-id}",
    containerFactory = "telegramKafkaListenerContainerFactory"
  )
  public void consume(TelegramMessageEvent event, Acknowledgment acknowledgment) {
    if (event == null) {
      log.warn("Received null Telegram event, skipping persistence and acknowledging offset");
      acknowledgment.acknowledge();
      return;
    }

    persistenceService.persist(event);
    acknowledgment.acknowledge();
  }
}