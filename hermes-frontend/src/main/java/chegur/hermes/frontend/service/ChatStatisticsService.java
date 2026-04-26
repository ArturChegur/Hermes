package chegur.hermes.frontend.service;

import chegur.hermes.frontend.dto.ChatPageData;
import chegur.hermes.frontend.dto.UserPageData;
import chegur.hermes.frontend.repository.ChatParticipantStatsRepository;
import chegur.hermes.frontend.repository.ChatStatsViewRepository;
import chegur.hermes.frontend.repository.TelegramChatLinkStatsRepository;
import chegur.hermes.frontend.repository.UserHourlyMessagePointRepository;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatStatisticsService {

  private final TelegramChatLinkStatsRepository chatLinkRepository;

  private final ChatStatsViewRepository chatStatsRepository;

  private final ChatParticipantStatsRepository participantStatsRepository;

  private final UserHourlyMessagePointRepository hourlyMessagePointRepository;

  public Optional<ChatPageData> getChatPage(String chatLink) {
    return chatLinkRepository.findActiveByCode(chatLink)
      .flatMap(link -> chatStatsRepository.findByTelegramChatId(link.getTelegramChatId())
        .map(stats -> new ChatPageData(
          link,
          stats,
          participantStatsRepository.findByTelegramChatId(stats.getTelegramChatId()),
          hourlyMessagePointRepository.findChatHourlyForWeek(stats.getTelegramChatId())
        )));
  }

  public Optional<UserPageData> getUserPage(String chatLink, Long userId) {
    return getChatPage(chatLink)
      .flatMap(chatPage -> participantStatsRepository.findByTelegramChatIdAndTelegramUserId(
          chatPage.getChatStats().getTelegramChatId(), userId)
        .map(userStats -> new UserPageData(
          chatPage,
          userStats,
          hourlyMessagePointRepository.findHourlyForWeek(chatPage.getChatStats().getTelegramChatId(), userId)
        )));
  }
}