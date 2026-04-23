package chegur.hermes.ingestor.configuration;

import chegur.hermes.ingestor.properties.TelegramBotProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@RequiredArgsConstructor
public class TelegramClientConfiguration {

  private final TelegramBotProperties telegramBotProperties;

  @Bean
  public TelegramClient telegramClient() {
    return new OkHttpTelegramClient(telegramBotProperties.getToken());
  }
}
