package chegur.hermes.ingestor.dispatcher;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class BotCommandHandler implements BotCommandOperations {

  @Override
  public void getStatisticLinkForCurrentChat(Update update) {

  }
}
