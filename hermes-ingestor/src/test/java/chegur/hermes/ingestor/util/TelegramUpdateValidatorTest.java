package chegur.hermes.ingestor.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

class TelegramUpdateValidatorTest {

  @Test
  void hasTextMessageShouldReturnTrueForNullUpdate() {
    assertThat(TelegramUpdateValidator.hasTextMessage(null)).isTrue();
  }

  @Test
  void hasTextMessageShouldReturnTrueWhenMessageIsMissing() {
    assertThat(TelegramUpdateValidator.hasTextMessage(new Update())).isTrue();
  }

  @Test
  void hasTextMessageShouldReturnTrueForBlankText() {
    Update update = updateWithText("   ");

    assertThat(TelegramUpdateValidator.hasTextMessage(update)).isTrue();
  }

  @Test
  void hasTextMessageShouldReturnFalseForNonBlankText() {
    Update update = updateWithText("hello");

    assertThat(TelegramUpdateValidator.hasTextMessage(update)).isFalse();
  }

  @Test
  void isCommandMessageShouldReturnTrueForValidCommandEntity() {
    MessageEntity messageEntity = new MessageEntity("bot_command", 0, "getlink".length());
    Update update = updateWithTextAndEntities("getlink", List.of(messageEntity));

    assertThat(TelegramUpdateValidator.isCommandMessage(update)).isTrue();
  }

  @Test
  void isCommandMessageShouldReturnFalseWhenEntityTypeIsNotCommand() {
    MessageEntity messageEntity = new MessageEntity("mention", 0, "@hermes".length());
    Update update = updateWithTextAndEntities("@hermes", List.of(messageEntity));

    assertThat(TelegramUpdateValidator.isCommandMessage(update)).isFalse();
  }

  @Test
  void isCommandMessageShouldReturnFalseForUnknownCommand() {
    MessageEntity messageEntity = new MessageEntity("bot_command", 0, "unknown".length());
    Update update = updateWithTextAndEntities("unknown", List.of(messageEntity));

    assertThat(TelegramUpdateValidator.isCommandMessage(update)).isFalse();
  }

  @Test
  void isCommandMessageShouldReturnFalseWhenOffsetIsNotZero() {
    MessageEntity messageEntity = new MessageEntity("bot_command", 1, "getlink".length());
    Update update = updateWithTextAndEntities("/getlink", List.of(messageEntity));

    assertThat(TelegramUpdateValidator.isCommandMessage(update)).isFalse();
  }

  @Test
  void isCommandMessageShouldReturnFalseWhenEntityLengthIsZero() {
    MessageEntity messageEntity = new MessageEntity("bot_command", 0, 0);
    Update update = updateWithTextAndEntities("getlink", List.of(messageEntity));

    assertThat(TelegramUpdateValidator.isCommandMessage(update)).isFalse();
  }

  private Update updateWithText(String text) {
    return updateWithTextAndEntities(text, null);
  }

  private Update updateWithTextAndEntities(String text, List<MessageEntity> entities) {
    Message message = new Message();
    message.setText(text);
    message.setEntities(entities);

    Update update = new Update();
    update.setMessage(message);

    return update;
  }
}
