package chegur.hermes.frontend.model.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@AllArgsConstructor
@Table("v_chat_user_stats")
public class ChatParticipantStatsView {

  @Id
  @Column("user_ref")
  private Long userRef;

  @Column("chat_ref")
  private Long chatRef;

  @Column("telegram_chat_id")
  private Long telegramChatId;

  @Column("telegram_user_id")
  private Long telegramUserId;

  @Column("display_name")
  private String displayName;

  @Column("total_messages")
  private Long totalMessages;

  @Column("messages_last_24_hours")
  private Long messagesLast24Hours;

  @Column("messages_last_1_hour")
  private Long messagesLast1Hour;
}