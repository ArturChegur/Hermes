package chegur.hermes.analytics.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import chegur.hermes.analytics.dto.TelegramMessageEvent;
import chegur.hermes.analytics.model.TelegramChatEntity;
import chegur.hermes.analytics.model.TelegramMessageEntity;
import chegur.hermes.analytics.model.TelegramUserEntity;
import chegur.hermes.analytics.repository.TelegramChatRepository;
import chegur.hermes.analytics.repository.TelegramMessageRepository;
import chegur.hermes.analytics.repository.TelegramUserRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelegramMessagePersistenceServiceTest {

  @Mock
  private TelegramUserRepository telegramUserRepository;

  @Mock
  private TelegramChatRepository telegramChatRepository;

  @Mock
  private TelegramMessageRepository telegramMessageRepository;

  @InjectMocks
  private TelegramMessagePersistenceService service;

  @Test
  void persistShouldIgnoreNullEvent() {
    service.persist(null);

    verifyNoInteractions(telegramUserRepository, telegramChatRepository, telegramMessageRepository);
  }

  @Test
  void persistShouldIgnoreBlankTextMessage() {
    TelegramMessageEvent event = new TelegramMessageEvent();
    event.setText("   ");
    event.setChatId(10L);

    service.persist(event);

    verifyNoInteractions(telegramUserRepository, telegramChatRepository, telegramMessageRepository);
  }

  @Test
  void persistShouldThrowWhenChatIdMissing() {
    TelegramMessageEvent event = new TelegramMessageEvent();
    event.setText("hello");

    assertThrows(IllegalStateException.class, () -> service.persist(event));
  }

  @Test
  void persistShouldSaveMessageWhenEventIsNew() {
    TelegramMessageEvent event = createEvent();

    when(telegramChatRepository.findByTelegramChatId(100L)).thenReturn(Optional.empty());
    when(telegramChatRepository.save(any(TelegramChatEntity.class))).thenReturn(
      TelegramChatEntity.builder()
        .id(10L)
        .telegramChatId(100L)
        .chatType("group")
        .chatTitle("Hermes")
        .chatUsername("hermes_chat")
        .build()
    );

    when(telegramUserRepository.findByTelegramUserId(200L)).thenReturn(Optional.empty());
    when(telegramUserRepository.save(any(TelegramUserEntity.class))).thenReturn(
      TelegramUserEntity.builder()
        .id(20L)
        .telegramUserId(200L)
        .username("user")
        .firstName("First")
        .lastName("Last")
        .languageCode("ru")
        .build()
    );

    when(telegramMessageRepository.findByTelegramUpdateId(1L)).thenReturn(Optional.empty());
    when(telegramMessageRepository.findByChatRefAndTelegramMessageId(10L, 11)).thenReturn(Optional.empty());

    service.persist(event);

    ArgumentCaptor<TelegramMessageEntity> messageCaptor = ArgumentCaptor.forClass(TelegramMessageEntity.class);
    verify(telegramMessageRepository).save(messageCaptor.capture());

    TelegramMessageEntity saved = messageCaptor.getValue();
    org.assertj.core.api.Assertions.assertThat(saved.getTelegramUpdateId()).isEqualTo(1L);
    org.assertj.core.api.Assertions.assertThat(saved.getTelegramMessageId()).isEqualTo(11);
    org.assertj.core.api.Assertions.assertThat(saved.getText()).isEqualTo("hello");
    org.assertj.core.api.Assertions.assertThat(saved.getChatRef()).isEqualTo(10L);
    org.assertj.core.api.Assertions.assertThat(saved.getUserRef()).isEqualTo(20L);
  }

  @Test
  void persistShouldSkipMessageSaveWhenDuplicateByUpdateId() {
    TelegramMessageEvent event = createEvent();

    when(telegramChatRepository.findByTelegramChatId(100L)).thenReturn(Optional.empty());
    when(telegramChatRepository.save(any(TelegramChatEntity.class))).thenReturn(
      TelegramChatEntity.builder().id(10L).telegramChatId(100L).chatType("group").build()
    );

    when(telegramUserRepository.findByTelegramUserId(200L)).thenReturn(Optional.empty());
    when(telegramUserRepository.save(any(TelegramUserEntity.class))).thenReturn(
      TelegramUserEntity.builder().id(20L).telegramUserId(200L).build()
    );

    when(telegramMessageRepository.findByTelegramUpdateId(1L)).thenReturn(
      Optional.of(TelegramMessageEntity.builder().id(99L).telegramUpdateId(1L).build())
    );

    service.persist(event);

    verify(telegramMessageRepository, never()).save(any(TelegramMessageEntity.class));
  }

  @Test
  void persistShouldSkipMessageSaveWhenDuplicateByChatAndMessageId() {
    TelegramMessageEvent event = createEvent();
    event.setUpdateId(null);

    when(telegramChatRepository.findByTelegramChatId(100L)).thenReturn(Optional.empty());
    when(telegramChatRepository.save(any(TelegramChatEntity.class))).thenReturn(
      TelegramChatEntity.builder().id(10L).telegramChatId(100L).chatType("group").build()
    );

    when(telegramUserRepository.findByTelegramUserId(200L)).thenReturn(Optional.empty());
    when(telegramUserRepository.save(any(TelegramUserEntity.class))).thenReturn(
      TelegramUserEntity.builder().id(20L).telegramUserId(200L).build()
    );

    when(telegramMessageRepository.findByChatRefAndTelegramMessageId(10L, 11)).thenReturn(
      Optional.of(TelegramMessageEntity.builder().id(100L).chatRef(10L).telegramMessageId(11).build())
    );

    service.persist(event);

    verify(telegramMessageRepository, never()).save(any(TelegramMessageEntity.class));
  }

  private TelegramMessageEvent createEvent() {
    TelegramMessageEvent event = new TelegramMessageEvent();
    event.setUpdateId(1L);
    event.setMessageId(11);
    event.setMessageDate(Instant.now());
    event.setText("hello");

    event.setChatId(100L);
    event.setChatType("group");
    event.setChatTitle("Hermes");
    event.setChatUsername("hermes_chat");

    event.setUserId(200L);
    event.setUsername("user");
    event.setFirstName("First");
    event.setLastName("Last");
    event.setLanguageCode("ru");
    return event;
  }
}
