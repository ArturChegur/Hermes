package chegur.hermes.ingestor.dispatcher;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotCommandOperations {

  void getStatisticLinkForCurrentChat(Update update);
}