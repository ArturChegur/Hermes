package chegur.hermes.frontend.dto;

import chegur.hermes.frontend.model.entity.ChatLinkDetailsEntity;

import chegur.hermes.frontend.model.entity.UserHourlyMessagePointEntity;
import chegur.hermes.frontend.model.view.ChatParticipantStatsView;
import chegur.hermes.frontend.model.view.ChatStatsView;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatPageDataDto {

  ChatLinkDetailsEntity link;

  ChatStatsView chatStats;

  List<ChatParticipantStatsView> participants;

  List<UserHourlyMessagePointEntity> hourlyPoints;
}