package chegur.hermes.ingestor.service;

import chegur.hermes.ingestor.command.BotCommands;
import chegur.hermes.ingestor.dispatcher.BotCommandOperations;

import chegur.hermes.ingestor.producer.TelegramUpdateKafkaProducer;
import chegur.hermes.ingestor.util.TelegramUpdateValidator;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

@Slf4j
@Service
public class TelegramProcessingService {

  private final TelegramUpdateKafkaProducer telegramUpdateKafkaProducer;

  private final Map<String, Consumer<Update>> commandHandlers;

  @Getter
  private final List<BotCommand> botCommands = List.of(
    new BotCommand(BotCommands.GET_LINK.getValue(), BotCommands.GET_LINK.getDescription())
  );

  public TelegramProcessingService(BotCommandOperations botCommandHandler,
                                   TelegramUpdateKafkaProducer telegramUpdateKafkaProducer) {
    this.telegramUpdateKafkaProducer = telegramUpdateKafkaProducer;

    this.commandHandlers = Map.of(
      BotCommands.GET_LINK.getValue(),
      botCommandHandler::getStatisticLinkForCurrentChat
    );
  }

  public void processUpdate(Update update) {
    if (TelegramUpdateValidator.hasTextMessage(update)) {
      return;
    }

    TelegramUpdateValidator.extractKnownCommand(update)
      .ifPresent(command -> {
        Consumer<Update> handler = commandHandlers.get(command);
        if (handler == null) {
          log.warn("No handler registered for command: {}", command);
          return;
        }

        handler.accept(update);
      });

    telegramUpdateKafkaProducer.ingest(update);
  }
}