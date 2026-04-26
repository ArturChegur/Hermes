package chegur.hermes.ingestor.util;

import chegur.hermes.ingestor.command.BotCommands;

import java.util.List;
import java.util.Optional;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@UtilityClass
public final class TelegramUpdateValidator {

  public static boolean hasTextMessage(Update update) {
    return update == null
      || !update.hasMessage()
      || update.getMessage() == null
      || !update.getMessage().hasText()
      || !StringUtils.hasText(update.getMessage().getText());
  }

  public static boolean isCommandMessage(Update update) {
    return extractKnownCommand(update).isPresent();
  }

  public static Optional<String> extractKnownCommand(Update update) {
    if (hasTextMessage(update)) {
      return Optional.empty();
    }

    Message message = update.getMessage();
    List<MessageEntity> entities = message.getEntities();

    if (entities == null || entities.isEmpty()) {
      return Optional.empty();
    }

    MessageEntity firstEntity = entities.getFirst();
    if (firstEntity == null) {
      return Optional.empty();
    }

    if (!"bot_command".equals(firstEntity.getType())) {
      return Optional.empty();
    }

    if (firstEntity.getOffset() != 0) {
      return Optional.empty();
    }

    String text = message.getText();
    int endIndex = firstEntity.getOffset() + firstEntity.getLength();

    if (endIndex <= 0 || endIndex > text.length()) {
      return Optional.empty();
    }

    String rawCommand = text.substring(0, endIndex);
    if (!StringUtils.hasText(rawCommand)) {
      return Optional.empty();
    }

    String normalizedCommand = normalizeCommand(rawCommand);

    if (!isKnownCommand(normalizedCommand)) {
      return Optional.empty();
    }

    return Optional.of(normalizedCommand);
  }

  private static String normalizeCommand(String command) {
    String normalized = command.trim();

    if (normalized.startsWith("/")) {
      normalized = normalized.substring(1);
    }

    int mentionSeparatorIndex = normalized.indexOf('@');
    if (mentionSeparatorIndex > 0) {
      normalized = normalized.substring(0, mentionSeparatorIndex);
    }

    return normalized;
  }

  private static boolean isKnownCommand(String command) {
    for (BotCommands botCommand : BotCommands.values()) {
      if (botCommand.getValue().equals(command)) {
        return true;
      }
    }

    return false;
  }
}