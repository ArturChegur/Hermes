package chegur.hermes.ingestor.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;

import chegur.hermes.ingestor.dto.TelegramMessageEvent;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

class TelegramMessageEventMapperTest {

  private final TelegramMessageEventMapper mapper = new TelegramMessageEventMapper();

  @Test
  void mapTextMessageShouldReturnEmptyForNullUpdate() {
    assertThat(mapper.mapTextMessage(null)).isEmpty();
  }

  @Test
  void mapTextMessageShouldReturnEmptyWhenUpdateHasNoMessage() {
    assertThat(mapper.mapTextMessage(new Update())).isEmpty();
  }

  @Test
  void mapTextMessageShouldReturnEmptyForBlankText() {
    Message message = new Message();
    message.setText("   ");

    Update update = new Update();
    update.setMessage(message);

    assertThat(mapper.mapTextMessage(update)).isEmpty();
  }

  @Test
  void mapTextMessageShouldMapAllFieldsForValidTextMessage() {
    User user = User.builder()
      .id(987654321L)
      .isBot(false)
      .userName("alice")
      .firstName("Alice")
      .lastName("Cooper")
      .languageCode("ru")
      .build();

    Chat chat = Chat.builder()
      .id(123456789L)
      .type("group")
      .title("Hermes Chat")
      .userName("hermes_group")
      .build();

    Message message = new Message();
    message.setMessageId(55);
    message.setDate(1_700_000_000);
    message.setText("hello world");
    message.setFrom(user);
    message.setChat(chat);

    Update update = new Update();
    update.setUpdateId(42);
    update.setMessage(message);

    Optional<TelegramMessageEvent> mapped = mapper.mapTextMessage(update);

    assertThat(mapped).isPresent();
    TelegramMessageEvent event = mapped.orElseThrow();

    assertThat(event.getUpdateId()).isEqualTo(42L);
    assertThat(event.getMessageId()).isEqualTo(55);
    assertThat(event.getMessageDate()).isEqualTo(Instant.ofEpochSecond(1_700_000_000));
    assertThat(event.getText()).isEqualTo("hello world");
    assertThat(event.getUserId()).isEqualTo(987654321L);
    assertThat(event.getUsername()).isEqualTo("alice");
    assertThat(event.getFirstName()).isEqualTo("Alice");
    assertThat(event.getLastName()).isEqualTo("Cooper");
    assertThat(event.getLanguageCode()).isEqualTo("ru");
    assertThat(event.getChatId()).isEqualTo(123456789L);
    assertThat(event.getChatType()).isEqualTo("group");
    assertThat(event.getChatTitle()).isEqualTo("Hermes Chat");
    assertThat(event.getChatUsername()).isEqualTo("hermes_group");
  }

  @Test
  void mapTextMessageShouldMapNullableFieldsWhenSenderAndChatAreAbsent() {
    Message message = new Message();
    message.setMessageId(77);
    message.setDate(null);
    message.setText("plain text");
    message.setFrom(null);
    message.setChat(null);

    Update update = new Update();
    update.setUpdateId(null);
    update.setMessage(message);

    TelegramMessageEvent event = mapper.mapTextMessage(update).orElseThrow();

    assertThat(event.getUpdateId()).isNull();
    assertThat(event.getMessageDate()).isNull();
    assertThat(event.getUserId()).isNull();
    assertThat(event.getUsername()).isNull();
    assertThat(event.getFirstName()).isNull();
    assertThat(event.getLastName()).isNull();
    assertThat(event.getLanguageCode()).isNull();
    assertThat(event.getChatId()).isNull();
    assertThat(event.getChatType()).isNull();
    assertThat(event.getChatTitle()).isNull();
    assertThat(event.getChatUsername()).isNull();
  }
}
