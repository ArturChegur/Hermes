package chegur.hermes.ingestor.service;

import chegur.hermes.ingestor.dto.TelegramMessageEvent;
import chegur.hermes.ingestor.mapper.TelegramMessageEventMapper;
import chegur.hermes.ingestor.properties.KafkaIngestorProperties;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramUpdateKafkaService {

  private final KafkaTemplate<String, TelegramMessageEvent> kafkaTemplate;

  private final TelegramMessageEventMapper mapper;

  private final KafkaIngestorProperties kafkaProperties;

  public void ingest(Update update) {
    mapper.mapTextMessage(update).ifPresent(event -> {
      String key = event.getUpdateId() != null
        ? String.valueOf(event.getUpdateId())
        : UUID.randomUUID().toString();

      kafkaTemplate.send(kafkaProperties.getUpdatesTopic(), key, event)
        .whenComplete((result, ex) -> {
          if (ex != null) {
            log.error("Failed to publish telegram event. key = {}, topic = {}",
              key, kafkaProperties.getUpdatesTopic(), ex);
          }
        });
    });
  }
}