package chegur.hermes.ingestor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import chegur.hermes.ingestor.model.TelegramChatLinkEntity;
import chegur.hermes.ingestor.properties.FrontendLinkProperties;
import chegur.hermes.ingestor.repository.TelegramChatLinkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelegramChatLinkServiceTest {

  @Mock
  private FrontendLinkProperties frontendLinkProperties;

  @Mock
  private TelegramChatLinkRepository repository;

  @InjectMocks
  private TelegramChatLinkService service;

  @Test
  void createLinkShouldReturnExistingActiveLinkWithoutSavingNewEntity() {
    Long chatId = 101L;
    TelegramChatLinkEntity existing = TelegramChatLinkEntity.builder()
      .id(1L)
      .code("existing-code")
      .chatId(chatId)
      .createdAt(LocalDateTime.now().minusMinutes(5))
      .expiresAt(LocalDateTime.now().plusMinutes(20))
      .build();

    when(frontendLinkProperties.getBaseUrl()).thenReturn("https://frontend");
    when(repository.findActiveByChatId(eq(chatId), any(LocalDateTime.class))).thenReturn(Optional.of(existing));

    String result = service.createLink(chatId);

    assertThat(result).isEqualTo("https://frontend/existing-code");
    verify(repository, never()).save(any());
  }

  @Test
  void createLinkShouldCreateAndPersistNewTokenWhenNoActiveTokenExists() {
    Long chatId = 202L;

    when(frontendLinkProperties.getBaseUrl()).thenReturn("https://frontend");
    when(repository.findActiveByChatId(eq(chatId), any(LocalDateTime.class))).thenReturn(Optional.empty());
    when(repository.save(any(TelegramChatLinkEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    String result = service.createLink(chatId);

    assertThat(result).startsWith("https://frontend/");
    String code = result.substring("https://frontend/".length());
    assertThat(code).isNotBlank();

    ArgumentCaptor<TelegramChatLinkEntity> entityCaptor = ArgumentCaptor.forClass(TelegramChatLinkEntity.class);
    verify(repository).save(entityCaptor.capture());

    TelegramChatLinkEntity savedEntity = entityCaptor.getValue();
    assertThat(savedEntity.getChatId()).isEqualTo(chatId);
    assertThat(savedEntity.getCode()).isEqualTo(code);
    assertThat(savedEntity.getCreatedAt()).isNotNull();
    assertThat(savedEntity.getExpiresAt()).isEqualTo(savedEntity.getCreatedAt().plusMinutes(60));
  }
}
