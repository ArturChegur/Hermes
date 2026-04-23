package chegur.hermes.ingestor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import chegur.hermes.ingestor.dispatcher.BotCommandOperations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ExtendWith(MockitoExtension.class)
class TelegramProcessingServiceTest {

  @Mock
  private BotCommandOperations botCommandOperations;

  @Mock
  private TelegramUpdateKafkaService telegramUpdateKafkaService;

  @InjectMocks
  private TelegramProcessingService telegramProcessingService;

  @Test
  void processUpdateShouldIgnoreUpdateWithoutTextMessage() {
    Update update = new Update();

    telegramProcessingService.processUpdate(update);

    verify(botCommandOperations, never()).getStatisticLinkForCurrentChat(update);
    verify(telegramUpdateKafkaService, never()).ingest(update);
  }

  @Test
  void processUpdateShouldRouteGetLinkCommandAndIngestUpdate() {
    Update update = commandUpdate("getlink");

    telegramProcessingService.processUpdate(update);

    verify(botCommandOperations).getStatisticLinkForCurrentChat(update);
    verify(telegramUpdateKafkaService).ingest(update);
  }

  @Test
  void processUpdateShouldIngestNonCommandTextMessage() {
    Update update = plainTextUpdate("hello");

    telegramProcessingService.processUpdate(update);

    verify(botCommandOperations, never()).getStatisticLinkForCurrentChat(update);
    verify(telegramUpdateKafkaService).ingest(update);
  }

  @Test
  void processUpdateShouldTreatUnknownCommandAsRegularTextAndIngest() {
    Update update = commandUpdate("unknown");

    telegramProcessingService.processUpdate(update);

    verify(botCommandOperations, never()).getStatisticLinkForCurrentChat(update);
    verify(telegramUpdateKafkaService).ingest(update);
  }

  @Test
  void getBotCommandsShouldExposeConfiguredGetLinkCommand() {
    assertThat(telegramProcessingService.getBotCommands())
      .hasSize(1)
      .first()
      .satisfies(command -> {
        assertThat(command.getCommand()).isEqualTo("getlink");
        assertThat(command.getDescription()).isEqualTo("Сгенерировать ссылку на статистику чата");
      });
  }

  @Test
  void processUpdateShouldNotIngestWhenCommandHandlerThrowsException() {
    Update update = commandUpdate("getlink");
    doThrow(new IllegalStateException("command failed"))
      .when(botCommandOperations)
      .getStatisticLinkForCurrentChat(update);

    org.assertj.core.api.Assertions.assertThatThrownBy(() -> telegramProcessingService.processUpdate(update))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("command failed");

    verify(telegramUpdateKafkaService, never()).ingest(update);
  }

  private Update plainTextUpdate(String text) {
    Message message = new Message();
    message.setText(text);
    message.setChat(Chat.builder().id(1L).type("group").build());

    Update update = new Update();
    update.setMessage(message);

    return update;
  }

  private Update commandUpdate(String commandText) {
    MessageEntity entity = new MessageEntity("bot_command", 0, commandText.length());
    Message message = new Message();
    message.setText(commandText);
    message.setEntities(java.util.List.of(entity));
    message.setChat(Chat.builder().id(1L).type("group").build());

    Update update = new Update();
    update.setMessage(message);

    return update;
  }
}
