package chegur.hermes.ingestor.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import chegur.hermes.ingestor.dto.TelegramMessageEvent;
import chegur.hermes.ingestor.mapper.TelegramMessageEventMapper;
import chegur.hermes.ingestor.properties.KafkaIngestorProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;

@ExtendWith(MockitoExtension.class)
class TelegramUpdateKafkaServiceTest {

  @Mock
  private KafkaTemplate<String, TelegramMessageEvent> kafkaTemplate;

  @Mock
  private TelegramMessageEventMapper mapper;

  @Mock
  private KafkaIngestorProperties kafkaProperties;

  @InjectMocks
  private TelegramUpdateKafkaService service;

  @Test
  void ingestShouldDoNothingWhenMapperReturnsEmpty() {
    Update update = new Update();
    when(mapper.mapTextMessage(update)).thenReturn(Optional.empty());

    service.ingest(update);

    verify(kafkaTemplate, never()).send(any(), any(), any());
  }

  @Test
  void ingestShouldUseUpdateIdAsKafkaKeyWhenPresent() {
    Update update = new Update();
    TelegramMessageEvent event = new TelegramMessageEvent();
    event.setUpdateId(99L);

    when(mapper.mapTextMessage(update)).thenReturn(Optional.of(event));
    when(kafkaProperties.getUpdatesTopic()).thenReturn("telegram-updates");
    when(kafkaTemplate.send("telegram-updates", "99", event)).thenReturn(CompletableFuture.completedFuture(null));

    service.ingest(update);

    verify(kafkaTemplate).send("telegram-updates", "99", event);
  }

  @Test
  void ingestShouldGenerateRandomKafkaKeyWhenUpdateIdIsMissing() {
    Update update = new Update();
    TelegramMessageEvent event = new TelegramMessageEvent();
    event.setUpdateId(null);

    when(mapper.mapTextMessage(update)).thenReturn(Optional.of(event));
    when(kafkaProperties.getUpdatesTopic()).thenReturn("telegram-updates");
    when(kafkaTemplate.send(eq("telegram-updates"), any(String.class), eq(event)))
      .thenReturn(CompletableFuture.completedFuture(null));

    service.ingest(update);

    verify(kafkaTemplate).send(eq("telegram-updates"), any(String.class), eq(event));
  }

  @Test
  void ingestShouldAttachCompletionHandlerForKafkaSendResult() {
    Update update = new Update();
    TelegramMessageEvent event = new TelegramMessageEvent();
    event.setUpdateId(101L);
    CompletableFuture<Object> future = new CompletableFuture<>();

    when(mapper.mapTextMessage(update)).thenReturn(Optional.of(event));
    when(kafkaProperties.getUpdatesTopic()).thenReturn("telegram-updates");
    when(kafkaTemplate.send("telegram-updates", "101", event)).thenReturn((CompletableFuture) future);

    service.ingest(update);

    verify(kafkaTemplate).send("telegram-updates", "101", event);
  }
}
