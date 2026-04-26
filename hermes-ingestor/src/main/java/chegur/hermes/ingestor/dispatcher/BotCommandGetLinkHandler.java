package chegur.hermes.ingestor.dispatcher;

import chegur.hermes.ingestor.command.BotCommands;
import chegur.hermes.ingestor.service.TelegramChatLinkService;
import chegur.hermes.ingestor.service.TelegramMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class BotCommandGetLinkHandler implements BotCommandHandler {

  private final TelegramChatLinkService telegramChatLinkService;

  private final TelegramMessageSender telegramMessageSender;

  @Override
  public void handle(Update update) {
    String chatLink = telegramChatLinkService.createLink(update.getMessage().getChatId());
    telegramMessageSender.sendMessage(update.getMessage().getChatId(), chatLink);
  }

  @Override
  public BotCommands getCommand() {
    return BotCommands.GET_LINK;
  }
}