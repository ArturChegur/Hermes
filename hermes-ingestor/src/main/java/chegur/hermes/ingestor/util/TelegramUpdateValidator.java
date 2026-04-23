package chegur.hermes.ingestor.util;

import chegur.hermes.ingestor.command.BotCommands;

import java.util.List;

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
    if (hasTextMessage(update)) {
      return false;
    }

    Message message = update.getMessage();
    List<MessageEntity> entities = message.getEntities();

    if (entities == null || entities.isEmpty()) {
      return false;
    }

    MessageEntity firstEntity = entities.getFirst();
    if (firstEntity == null) {
      return false;
    }

    if (!"bot_command".equals(firstEntity.getType())) {
      return false;
    }

    if (firstEntity.getOffset() != 0) {
      return false;
    }

    String text = message.getText();
    int endIndex = firstEntity.getOffset() + firstEntity.getLength();

    if (endIndex <= 0 || endIndex > text.length()) {
      return false;
    }

    String command = text.substring(0, endIndex);
    if (!StringUtils.hasText(command)) {
      return false;
    }

    return isKnownCommand(command);
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