package chegur.hermes.frontend.dto;

import chegur.hermes.frontend.model.entity.UserHourlyMessagePointEntity;
import chegur.hermes.frontend.model.view.ChatParticipantStatsView;

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
public class UserPageDataDto {

  ChatPageDataDto chatPage;

  ChatParticipantStatsView userStats;

  List<UserHourlyMessagePointEntity> hourlyPoints;
}