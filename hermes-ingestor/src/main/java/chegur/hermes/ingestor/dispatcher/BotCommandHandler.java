package chegur.hermes.ingestor.dispatcher;

import chegur.hermes.ingestor.command.BotCommands;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotCommandHandler {

  void handle(Update update);

  BotCommands getCommand();
}