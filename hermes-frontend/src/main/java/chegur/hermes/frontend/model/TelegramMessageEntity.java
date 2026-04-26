package chegur.hermes.frontend.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@AllArgsConstructor
@Table("telegram_message")
public class TelegramMessageEntity {

  @Id
  private Long id;

  @Column("telegram_update_id")
  private Long telegramUpdateId;

  @Column("telegram_message_id")
  private Integer telegramMessageId;

  @Column("message_date")
  private Instant messageDate;

  @Column("text")
  private String text;

  @Column("user_ref")
  private Long userRef;

  @Column("chat_ref")
  private Long chatRef;
}