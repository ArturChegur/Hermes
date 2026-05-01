package chegur.hermes.ingestor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramMessageService {

  private final TelegramClient telegramClient;

  public void sendMessage(Long chatId, String message) {
    SendMessage sendMessage = SendMessage.builder()
      .chatId(chatId.toString())
      .text(message)
      .build();

    executeSendMessage(chatId, sendMessage);
  }

  public void sendMessage(Long chatId, String message, Integer replyToMessageId) {
    SendMessage sendMessage = SendMessage.builder()
      .chatId(chatId.toString())
      .text(message)
      .replyToMessageId(replyToMessageId)
      .allowSendingWithoutReply(true)
      .build();

    executeSendMessage(chatId, sendMessage);
  }

  private void executeSendMessage(Long chatId, SendMessage sendMessage) {

    try {
      telegramClient.execute(sendMessage);
    } catch (TelegramApiException e) {
      log.error("Failed to send telegram link message, chatId = {}", chatId, e);
      throw new IllegalStateException("Failed to send telegram message", e);
    }
  }

  public void deleteMessage(Long chatId, Integer messageId) {
    DeleteMessage deleteMessage = DeleteMessage.builder()
      .chatId(chatId)
      .messageId(messageId)
      .build();

    try {
      telegramClient.execute(deleteMessage);
    } catch (TelegramApiException e) {
      log.error("Failed to delete telegram message, chatId = {}", chatId, e);
      throw new IllegalStateException("Failed to delete telegram message", e);
    }
  }
}