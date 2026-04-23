package chegur.hermes.ingestor.telegram;

import chegur.hermes.ingestor.configuration.TelegramClientConfiguration;
import chegur.hermes.ingestor.properties.TelegramBotProperties;

import chegur.hermes.ingestor.service.TelegramProcessingService;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class HermesLongPollingBot implements SpringLongPollingBot {

  private final TelegramBotProperties telegramBotProperties;

  private final TelegramProcessingService telegramProcessingService;

  private final TelegramClient telegramClient;

  @Override
  public String getBotToken() {
    return Objects.requireNonNull(telegramBotProperties).getToken();
  }

  @Override
  public LongPollingSingleThreadUpdateConsumer getUpdatesConsumer() {
    return this::consume;
  }

  @AfterBotRegistration
  public void configureCommands(BotSession ignoredSession) {
    try {
      SetMyCommands commands = SetMyCommands.builder()
        .commands(telegramProcessingService.getBotCommands())
        .build();

      telegramClient.execute(commands);
    } catch (TelegramApiException ex) {
      log.warn("Error while trying to configure commands for bot: {}", ex.getMessage());
    }
  }

  private void consume(Update update) {
    telegramProcessingService.processUpdate(update);
  }
}
