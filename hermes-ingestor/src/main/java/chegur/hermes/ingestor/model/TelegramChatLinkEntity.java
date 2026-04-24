package chegur.hermes.ingestor.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@AllArgsConstructor
@Table("telegram_chat_link")
public class TelegramChatLinkEntity {

  @Id
  private final Long id;

  @Column("code")
  private final String code;

  @Column("chat_id")
  private final Long chatId;

  @Column("created_at")
  private final LocalDateTime createdAt;

  @Column("expires_at")
  private final LocalDateTime expiresAt;
}
