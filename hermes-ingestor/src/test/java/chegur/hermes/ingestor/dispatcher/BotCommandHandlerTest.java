package chegur.hermes.ingestor.dispatcher;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import chegur.hermes.ingestor.service.TelegramChatLinkService;
import chegur.hermes.ingestor.service.TelegramMessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ExtendWith(MockitoExtension.class)
class BotCommandHandlerTest {

  @Mock
  private TelegramChatLinkService telegramChatLinkService;

  @Mock
  private TelegramMessageSender telegramMessageSender;

  @InjectMocks
  private BotCommandHandler botCommandHandler;

  @Test
  void getStatisticLinkForCurrentChatShouldCreateAndSendLink() {
    Long chatId = 444L;
    String expectedLink = "https://frontend/code";
    Update update = updateWithChatId(chatId);

    when(telegramChatLinkService.createLink(chatId)).thenReturn(expectedLink);

    botCommandHandler.getStatisticLinkForCurrentChat(update);

    verify(telegramChatLinkService).createLink(chatId);
    verify(telegramMessageSender).sendMessage(chatId, expectedLink);
  }

  private Update updateWithChatId(Long chatId) {
    Message message = new Message();
    message.setChat(Chat.builder().id(chatId).type("group").build());

    Update update = new Update();
    update.setMessage(message);

    return update;
  }
}
