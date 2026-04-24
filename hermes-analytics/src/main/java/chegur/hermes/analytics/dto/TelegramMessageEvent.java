package chegur.hermes.analytics.dto;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramMessageEvent {

  Long updateId;

  Integer messageId;

  Instant messageDate;

  String text;

  Long userId;

  String username;

  String firstName;

  String lastName;

  String languageCode;

  Long chatId;

  String chatType;

  String chatTitle;

  String chatUsername;
}