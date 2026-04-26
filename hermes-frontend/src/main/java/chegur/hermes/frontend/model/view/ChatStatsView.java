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
@Table("v_chat_stats")
public class ChatStatsView {

  @Id
  @Column("chat_ref")
  private Long chatRef;

  @Column("telegram_chat_id")
  private Long telegramChatId;

  @Column("chat_title")
  private String chatTitle;

  @Column("chat_username")
  private String chatUsername;

  @Column("chat_type")
  private String chatType;

  @Column("total_messages")
  private Long totalMessages;

  @Column("messages_last_24_hours")
  private Long messagesLast24Hours;

  @Column("messages_last_1_hour")
  private Long messagesLast1Hour;

  @Column("participants_count")
  private Long participantsCount;
}