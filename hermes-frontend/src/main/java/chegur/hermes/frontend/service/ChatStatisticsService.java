package chegur.hermes.frontend.service;

import chegur.hermes.frontend.dto.ChatPageDataDto;
import chegur.hermes.frontend.dto.UserPageDataDto;
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

  public Optional<ChatPageDataDto> getChatPage(String chatLink) {
    return chatLinkRepository.findActiveByCode(chatLink)
      .flatMap(link -> chatStatsRepository.findByTelegramChatId(link.getTelegramChatId())
        .map(stats -> ChatPageDataDto.builder()
            .link(link)
            .chatStats(stats)
            .participants(participantStatsRepository.findByTelegramChatId(stats.getTelegramChatId()))
            .hourlyPoints(hourlyMessagePointRepository.findChatHourlyForWeek(stats.getTelegramChatId()))
            .build()));
  }

  public Optional<UserPageDataDto> getUserPage(String chatLink, Long userId) {
    return getChatPage(chatLink)
      .flatMap(chatPage -> participantStatsRepository.findByTelegramChatIdAndTelegramUserId(
          chatPage.getChatStats().getTelegramChatId(), userId)
        .map(userStats -> UserPageDataDto.builder()
            .chatPage(chatPage)
            .userStats(userStats)
            .hourlyPoints( hourlyMessagePointRepository.findHourlyForWeek(chatPage.getChatStats().getTelegramChatId(), userId))
            .build()));
  }
}