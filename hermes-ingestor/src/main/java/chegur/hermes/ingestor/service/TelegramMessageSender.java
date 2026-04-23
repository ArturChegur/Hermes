package chegur.hermes.ingestor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramMessageSender {

  private final TelegramClient telegramClient;

  public void sendMessage(Long chatId, String message) {
    SendMessage sendMessage = SendMessage.builder()
      .chatId(chatId.toString())
      .text(message)
      .build();

    try {
      telegramClient.execute(sendMessage);
    } catch (TelegramApiException e) {
      log.error("Failed to send telegram link message. chatId = {}, error = {}", chatId, e.getMessage());
      throw new IllegalStateException("Failed to send telegram message", e);
    }
  }
}