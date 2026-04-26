package chegur.hermes.frontend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import chegur.hermes.frontend.dto.ChatLinkDetails;
import chegur.hermes.frontend.dto.ChatPageData;
import chegur.hermes.frontend.dto.ChatParticipantStatsView;
import chegur.hermes.frontend.dto.ChatStatsView;
import chegur.hermes.frontend.dto.UserHourlyMessagePoint;
import chegur.hermes.frontend.dto.UserPageData;
import chegur.hermes.frontend.repository.ChatParticipantStatsRepository;
import chegur.hermes.frontend.repository.ChatStatsViewRepository;
import chegur.hermes.frontend.repository.TelegramChatLinkStatsRepository;
import chegur.hermes.frontend.repository.UserHourlyMessagePointRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatStatisticsServiceTest {

  private static final String CHAT_LINK_CODE = "chat-link-code";
  private static final Long TELEGRAM_CHAT_ID = 12345L;
  private static final Long TELEGRAM_USER_ID = 67890L;

  @Mock
  private TelegramChatLinkStatsRepository chatLinkRepository;

  @Mock
  private ChatStatsViewRepository chatStatsRepository;

  @Mock
  private ChatParticipantStatsRepository participantStatsRepository;

  @Mock
  private UserHourlyMessagePointRepository hourlyMessagePointRepository;

  @InjectMocks
  private ChatStatisticsService service;

  @Test
  void getChatPageShouldReturnEmptyWhenLinkIsMissing() {
    when(chatLinkRepository.findActiveByCode(CHAT_LINK_CODE)).thenReturn(Optional.empty());

    Optional<ChatPageData> result = service.getChatPage(CHAT_LINK_CODE);

    assertThat(result).isEmpty();
    verify(chatLinkRepository).findActiveByCode(CHAT_LINK_CODE);
    verifyNoInteractions(chatStatsRepository, participantStatsRepository, hourlyMessagePointRepository);
  }

  @Test
  void getChatPageShouldReturnEmptyWhenStatsViewIsMissing() {
    ChatLinkDetails link = chatLink();
    when(chatLinkRepository.findActiveByCode(CHAT_LINK_CODE)).thenReturn(Optional.of(link));
    when(chatStatsRepository.findByTelegramChatId(TELEGRAM_CHAT_ID)).thenReturn(Optional.empty());

    Optional<ChatPageData> result = service.getChatPage(CHAT_LINK_CODE);

    assertThat(result).isEmpty();
    verify(chatLinkRepository).findActiveByCode(CHAT_LINK_CODE);
    verify(chatStatsRepository).findByTelegramChatId(TELEGRAM_CHAT_ID);
    verifyNoInteractions(participantStatsRepository, hourlyMessagePointRepository);
  }

  @Test
  void getChatPageShouldReturnPageDataWhenLinkAndStatsExist() {
    ChatLinkDetails link = chatLink();
    ChatStatsView chatStats = chatStats();
    List<ChatParticipantStatsView> participants = List.of(participant(TELEGRAM_USER_ID, "Alice"));
    List<UserHourlyMessagePoint> chatHourly = List.of(hourlyPoint(5));

    when(chatLinkRepository.findActiveByCode(CHAT_LINK_CODE)).thenReturn(Optional.of(link));
    when(chatStatsRepository.findByTelegramChatId(TELEGRAM_CHAT_ID)).thenReturn(Optional.of(chatStats));
    when(participantStatsRepository.findByTelegramChatId(TELEGRAM_CHAT_ID)).thenReturn(participants);
    when(hourlyMessagePointRepository.findChatHourlyForWeek(TELEGRAM_CHAT_ID)).thenReturn(chatHourly);

    Optional<ChatPageData> result = service.getChatPage(CHAT_LINK_CODE);

    assertThat(result).isPresent();
    ChatPageData pageData = result.orElseThrow();
    assertThat(pageData.getLink()).isSameAs(link);
    assertThat(pageData.getChatStats()).isSameAs(chatStats);
    assertThat(pageData.getParticipants()).containsExactlyElementsOf(participants);
    assertThat(pageData.getHourlyPoints()).containsExactlyElementsOf(chatHourly);

    verify(participantStatsRepository).findByTelegramChatId(TELEGRAM_CHAT_ID);
    verify(hourlyMessagePointRepository).findChatHourlyForWeek(TELEGRAM_CHAT_ID);
  }

  @Test
  void getUserPageShouldReturnEmptyWhenChatPageIsMissing() {
    when(chatLinkRepository.findActiveByCode(CHAT_LINK_CODE)).thenReturn(Optional.empty());

    Optional<UserPageData> result = service.getUserPage(CHAT_LINK_CODE, TELEGRAM_USER_ID);

    assertThat(result).isEmpty();
    verify(participantStatsRepository, never()).findByTelegramChatIdAndTelegramUserId(anyLong(), anyLong());
    verify(hourlyMessagePointRepository, never()).findHourlyForWeek(anyLong(), anyLong());
  }

  @Test
  void getUserPageShouldReturnEmptyWhenUserStatsAreMissing() {
    ChatLinkDetails link = chatLink();
    ChatStatsView chatStats = chatStats();

    when(chatLinkRepository.findActiveByCode(CHAT_LINK_CODE)).thenReturn(Optional.of(link));
    when(chatStatsRepository.findByTelegramChatId(TELEGRAM_CHAT_ID)).thenReturn(Optional.of(chatStats));
    when(participantStatsRepository.findByTelegramChatId(TELEGRAM_CHAT_ID)).thenReturn(List.of());
    when(hourlyMessagePointRepository.findChatHourlyForWeek(TELEGRAM_CHAT_ID)).thenReturn(List.of());
    when(participantStatsRepository.findByTelegramChatIdAndTelegramUserId(TELEGRAM_CHAT_ID, TELEGRAM_USER_ID))
      .thenReturn(Optional.empty());

    Optional<UserPageData> result = service.getUserPage(CHAT_LINK_CODE, TELEGRAM_USER_ID);

    assertThat(result).isEmpty();
    verify(participantStatsRepository).findByTelegramChatIdAndTelegramUserId(TELEGRAM_CHAT_ID, TELEGRAM_USER_ID);
    verify(hourlyMessagePointRepository, never()).findHourlyForWeek(anyLong(), anyLong());
  }

  @Test
  void getUserPageShouldReturnUserDataWhenUserStatsExist() {
    ChatLinkDetails link = chatLink();
    ChatStatsView chatStats = chatStats();
    ChatParticipantStatsView userStats = participant(TELEGRAM_USER_ID, "Alice");

    List<ChatParticipantStatsView> participants = List.of(userStats);
    List<UserHourlyMessagePoint> chatHourly = List.of(hourlyPoint(9));
    List<UserHourlyMessagePoint> userHourly = List.of(hourlyPoint(3));

    when(chatLinkRepository.findActiveByCode(CHAT_LINK_CODE)).thenReturn(Optional.of(link));
    when(chatStatsRepository.findByTelegramChatId(TELEGRAM_CHAT_ID)).thenReturn(Optional.of(chatStats));
    when(participantStatsRepository.findByTelegramChatId(TELEGRAM_CHAT_ID)).thenReturn(participants);
    when(hourlyMessagePointRepository.findChatHourlyForWeek(TELEGRAM_CHAT_ID)).thenReturn(chatHourly);
    when(participantStatsRepository.findByTelegramChatIdAndTelegramUserId(TELEGRAM_CHAT_ID, TELEGRAM_USER_ID))
      .thenReturn(Optional.of(userStats));
    when(hourlyMessagePointRepository.findHourlyForWeek(TELEGRAM_CHAT_ID, TELEGRAM_USER_ID)).thenReturn(userHourly);

    Optional<UserPageData> result = service.getUserPage(CHAT_LINK_CODE, TELEGRAM_USER_ID);

    assertThat(result).isPresent();
    UserPageData userPageData = result.orElseThrow();
    assertThat(userPageData.getChatPage()).isNotNull();
    assertThat(userPageData.getChatPage().getLink()).isSameAs(link);
    assertThat(userPageData.getChatPage().getChatStats()).isSameAs(chatStats);
    assertThat(userPageData.getChatPage().getParticipants()).containsExactlyElementsOf(participants);
    assertThat(userPageData.getChatPage().getHourlyPoints()).containsExactlyElementsOf(chatHourly);
    assertThat(userPageData.getUserStats()).isSameAs(userStats);
    assertThat(userPageData.getHourlyPoints()).containsExactlyElementsOf(userHourly);

    verify(hourlyMessagePointRepository).findHourlyForWeek(TELEGRAM_CHAT_ID, TELEGRAM_USER_ID);
  }

  private ChatLinkDetails chatLink() {
    return ChatLinkDetails.builder()
      .code(CHAT_LINK_CODE)
      .telegramChatId(TELEGRAM_CHAT_ID)
      .createdAt(LocalDateTime.now().minusDays(1))
      .expiresAt(LocalDateTime.now().plusDays(6))
      .build();
  }

  private ChatStatsView chatStats() {
    return new ChatStatsView(
      10L,
      TELEGRAM_CHAT_ID,
      "Hermes Chat",
      "hermes_chat",
      "supergroup",
      200L,
      42L,
      5L,
      8L
    );
  }

  private ChatParticipantStatsView participant(Long telegramUserId, String displayName) {
    return new ChatParticipantStatsView(
      20L,
      10L,
      TELEGRAM_CHAT_ID,
      telegramUserId,
      displayName,
      120L,
      20L,
      3L
    );
  }

  private UserHourlyMessagePoint hourlyPoint(long messagesCount) {
    return new UserHourlyMessagePoint(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0), messagesCount);
  }
}