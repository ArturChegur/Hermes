package chegur.hermes.analytics.service;

import chegur.hermes.analytics.dto.TelegramMessageEvent;
import chegur.hermes.analytics.model.TelegramChatEntity;
import chegur.hermes.analytics.model.TelegramMessageEntity;
import chegur.hermes.analytics.model.TelegramUserEntity;
import chegur.hermes.analytics.repository.TelegramChatRepository;
import chegur.hermes.analytics.repository.TelegramMessageRepository;
import chegur.hermes.analytics.repository.TelegramUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TelegramMessagePersistenceService {

  private final TelegramUserRepository telegramUserRepository;

  private final TelegramChatRepository telegramChatRepository;

  private final TelegramMessageRepository telegramMessageRepository;

  @Transactional
  public void persist(TelegramMessageEvent event) {
    if (event == null || event.getText() == null || event.getText().isBlank()) {
      return;
    }

    TelegramChatEntity chat = upsertChat(event);
    TelegramUserEntity user = upsertUser(event);

    if (messageAlreadyExists(event, chat.getId())) {
      return;
    }

    TelegramMessageEntity message = TelegramMessageEntity.builder()
      .telegramUpdateId(event.getUpdateId())
      .telegramMessageId(event.getMessageId())
      .messageDate(event.getMessageDate())
      .text(event.getText())
      .userRef(user == null ? null : user.getId())
      .chatRef(chat.getId())
      .build();

    telegramMessageRepository.save(message);
  }

  private TelegramUserEntity upsertUser(TelegramMessageEvent event) {
    if (event.getUserId() == null) {
      return null;
    }

    TelegramUserEntity user = telegramUserRepository.findByTelegramUserId(event.getUserId())
      .orElse(TelegramUserEntity.builder()
        .telegramUserId(event.getUserId())
        .username(event.getUsername())
        .firstName(event.getFirstName())
        .lastName(event.getLastName())
        .languageCode(event.getLanguageCode())
        .build());

    return telegramUserRepository.save(user);
  }

  private TelegramChatEntity upsertChat(TelegramMessageEvent event) {
    if (event.getChatId() == null) {
      throw new IllegalStateException("chatId is required");
    }

    TelegramChatEntity chat = telegramChatRepository.findByTelegramChatId(event.getChatId())
      .orElse(TelegramChatEntity.builder()
        .telegramChatId(event.getChatId())
        .chatType(event.getChatType() == null ? "unknown" : event.getChatType())
        .chatTitle(event.getChatTitle())
        .chatUsername(event.getChatUsername())
        .build());

    return telegramChatRepository.save(chat);
  }

  private boolean messageAlreadyExists(TelegramMessageEvent event, Long chatRef) {
    if (event.getUpdateId() != null
      && telegramMessageRepository.findByTelegramUpdateId(event.getUpdateId()).isPresent()) {
      return true;
    }

    return event.getMessageId() != null && telegramMessageRepository.findByChatRefAndTelegramMessageId(chatRef, event.getMessageId()).isPresent();
  }
}