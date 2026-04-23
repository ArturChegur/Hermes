package chegur.hermes.ingestor.dto;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
