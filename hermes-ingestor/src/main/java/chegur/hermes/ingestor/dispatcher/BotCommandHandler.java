package chegur.hermes.ingestor.dispatcher;

import chegur.hermes.ingestor.service.TelegramChatLinkService;
import chegur.hermes.ingestor.service.TelegramMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class BotCommandHandler implements BotCommandOperations {

  private final TelegramChatLinkService telegramChatLinkService;

  private final TelegramMessageSender telegramMessageSender;

  @Override
  public void getStatisticLinkForCurrentChat(Update update) {
    String chatLink = telegramChatLinkService.createLink(update.getMessage().getChatId());
    telegramMessageSender.sendMessage(update.getMessage().getChatId(), chatLink);
  }
}
