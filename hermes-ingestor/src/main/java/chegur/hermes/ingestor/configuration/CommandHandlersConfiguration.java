package chegur.hermes.ingestor.configuration;

import chegur.hermes.ingestor.command.BotCommands;

import chegur.hermes.ingestor.dispatcher.BotCommandHandler;

import java.util.Arrays;
import java.util.List;

import java.util.Map;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

@Configuration
public class CommandHandlersConfiguration {

  @Bean
  public Map<BotCommands, BotCommandHandler> getAllCommandsData(List<BotCommandHandler> botCommandHandlers) {
    return botCommandHandlers.stream()
      .collect(Collectors.toMap(BotCommandHandler::getCommand, Function.identity()));
  }

  @Bean
  public List<BotCommand> botCommandData() {
    return Arrays.stream(BotCommands.values())
      .map(botCommandData -> new BotCommand(botCommandData.getValue(), botCommandData.getDescription())).toList();
  }
}