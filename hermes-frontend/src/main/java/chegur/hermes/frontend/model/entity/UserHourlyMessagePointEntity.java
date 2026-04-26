package chegur.hermes.frontend.model.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@Table("v_chat_user_hourly_messages")
public class UserHourlyMessagePointEntity {

  @Id
  @Column("hour_start")
  private LocalDateTime hourStart;

  @Column("messages_count")
  private Long messagesCount;
}