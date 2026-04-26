package chegur.hermes.ingestor.service;

import chegur.hermes.ingestor.command.BotCommands;
import chegur.hermes.ingestor.dispatcher.BotCommandHandler;

import chegur.hermes.ingestor.producer.TelegramUpdateKafkaProducer;
import chegur.hermes.ingestor.util.TelegramUpdateValidator;

import java.util.Map;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramProcessingService {

  private final TelegramUpdateKafkaProducer telegramUpdateKafkaProducer;

  private final Map<BotCommands, BotCommandHandler> commandHandlers;

  public void processUpdate(Update update) {
    if (TelegramUpdateValidator.hasTextMessage(update)) {
      return;
    }

    TelegramUpdateValidator.extractKnownCommand(update)
      .map(BotCommands::getByName)
      .filter((Optional::isPresent))
      .map(Optional::get)
      .map(commandHandlers::get)
      .ifPresent(botCommandHandler -> botCommandHandler.handle(update));

    telegramUpdateKafkaProducer.ingest(update);
  }
}