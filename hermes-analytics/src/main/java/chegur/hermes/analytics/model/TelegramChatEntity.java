package chegur.hermes.analytics.model;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@AllArgsConstructor
@Table("telegram_chat")
public class TelegramChatEntity {

  @Id
  private Long id;

  @Column("telegram_chat_id")
  private Long telegramChatId;

  @Column("chat_type")
  private String chatType;

  @Column("chat_title")
  private String chatTitle;

  @Column("chat_username")
  private String chatUsername;
}