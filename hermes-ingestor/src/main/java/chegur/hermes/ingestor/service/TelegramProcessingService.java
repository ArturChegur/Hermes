package chegur.hermes.ingestor.service;

import chegur.hermes.ingestor.command.BotCommands;
import chegur.hermes.ingestor.dispatcher.BotCommandOperations;

import chegur.hermes.ingestor.util.TelegramUpdateValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class TelegramProcessingService {

  private final BotCommandOperations botCommandHandler;

  private final List<BotCommand> botCommands = List.of(
    new BotCommand(BotCommands.GET_LINK.getValue(), BotCommands.GET_LINK.getDescription())
  );

  private final Map<String, Consumer<Update>> commandHandlers = Map.of(
    BotCommands.GET_LINK.getValue(), botCommandHandler::getStatisticLinkForCurrentChat
  );

  public void processUpdate(Update update) {
    if (TelegramUpdateValidator.hasTextMessage(update)) {
      return;
    }

    if (TelegramUpdateValidator.isCommandMessage(update)) {
      String command = extractCommand(update).orElseThrow();
      commandHandlers.get(command).accept(update);
    }
  }

  private Optional<String> extractCommand(Update update) {
    Message message = update.getMessage();
    MessageEntity firstEntity = message.getEntities().getFirst();

    String rawCommand = message.getText()
      .substring(firstEntity.getOffset(), firstEntity.getOffset() + firstEntity.getLength());

    return Optional.of(rawCommand);
  }
}
