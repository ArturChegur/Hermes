package chegur.hermes.ingestor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
class TelegramMessageSenderTest {

  @Mock
  private TelegramClient telegramClient;

  @InjectMocks
  private TelegramMessageSender telegramMessageSender;

  @Test
  void sendMessageShouldCreateAndSendTelegramMessage() throws Exception {
    Long chatId = 12345L;
    String text = "hello";

    telegramMessageSender.sendMessage(chatId, text);

    ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramClient).execute(captor.capture());

    SendMessage command = captor.getValue();
    assertThat(command.getChatId()).isEqualTo(chatId.toString());
    assertThat(command.getText()).isEqualTo(text);
  }

  @Test
  void sendMessageShouldWrapTelegramApiExceptionIntoIllegalStateException() throws Exception {
    when(telegramClient.execute(any(SendMessage.class))).thenThrow(new TelegramApiException("boom"));

    assertThatThrownBy(() -> telegramMessageSender.sendMessage(42L, "payload"))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("Failed to send telegram message")
      .hasCauseInstanceOf(TelegramApiException.class);
  }
}
