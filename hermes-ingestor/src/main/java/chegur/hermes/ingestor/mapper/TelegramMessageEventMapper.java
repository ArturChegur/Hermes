package chegur.hermes.ingestor.mapper;

import chegur.hermes.ingestor.dto.TelegramMessageEvent;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
public class TelegramMessageEventMapper {

  public Optional<TelegramMessageEvent> mapTextMessage(Update update) {
    if (update == null || !update.hasMessage()) {
      return Optional.empty();
    }

    Message message = update.getMessage();
    if (!message.hasText() || !StringUtils.hasText(message.getText())) {
      return Optional.empty();
    }

    User from = message.getFrom();

    TelegramMessageEvent event = new TelegramMessageEvent();
    event.setUpdateId(toLong(update.getUpdateId()));
    event.setMessageId(message.getMessageId());
    event.setMessageDate(message.getDate() == null ? null : Instant.ofEpochSecond(message.getDate()));
    event.setText(message.getText());

    event.setUserId(from == null ? null : from.getId());
    event.setUsername(from == null ? null : from.getUserName());
    event.setFirstName(from == null ? null : from.getFirstName());
    event.setLastName(from == null ? null : from.getLastName());
    event.setLanguageCode(from == null ? null : from.getLanguageCode());

    event.setChatId(message.getChat() == null ? null : message.getChatId());
    event.setChatType(message.getChat() == null ? null : message.getChat().getType());
    event.setChatTitle(message.getChat() == null ? null : message.getChat().getTitle());
    event.setChatUsername(message.getChat() == null ? null : message.getChat().getUserName());

    return Optional.of(event);
  }

  private Long toLong(Integer value) {
    return value == null ? null : value.longValue();
  }
}