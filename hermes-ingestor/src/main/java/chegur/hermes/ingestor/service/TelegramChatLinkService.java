package chegur.hermes.ingestor.service;

import chegur.hermes.ingestor.model.TelegramChatLinkEntity;
import chegur.hermes.ingestor.properties.FrontendLinkProperties;
import chegur.hermes.ingestor.repositary.TelegramChatLinkRepository;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TelegramChatLinkService {

  private static final int TOKEN_BYTES = 24;

  private static final int TTL_MINUTES = 60;

  private final SecureRandom secureRandom = new SecureRandom();

  private final FrontendLinkProperties frontendLinkProperties;

  private final TelegramChatLinkRepository repository;

  @Transactional
  public String createLink(Long chatId) {
    LocalDateTime now = LocalDateTime.now();

    return repository.findActiveByChatId(chatId, now)
      .map(existing -> buildUrl(existing.getCode()))
      .orElseGet(() -> createAndSaveNewLink(chatId, now));
  }

  private String createAndSaveNewLink(Long chatId, LocalDateTime now) {
    TelegramChatLinkEntity token = TelegramChatLinkEntity.builder()
      .code(generateCode())
      .chatId(chatId)
      .createdAt(now)
      .expiresAt(now.plusMinutes(TTL_MINUTES))
      .build();

    TelegramChatLinkEntity saved = repository.save(token);

    return buildUrl(saved.getCode());
  }

  private String buildUrl(String code) {
    return frontendLinkProperties.getBaseUrl() + "/" + code;
  }

  private String generateCode() {
    byte[] bytes = new byte[TOKEN_BYTES];
    secureRandom.nextBytes(bytes);

    return Base64.getUrlEncoder()
      .withoutPadding()
      .encodeToString(bytes);
  }
}


