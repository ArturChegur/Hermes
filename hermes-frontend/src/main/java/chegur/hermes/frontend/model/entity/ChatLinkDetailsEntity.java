package chegur.hermes.frontend.model.entity;

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
public class ChatLinkDetailsEntity {

  @Id
  @Column("code")
  private String code;

  @Column("telegram_chat_id")
  private Long telegramChatId;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("expires_at")
  private LocalDateTime expiresAt;
}