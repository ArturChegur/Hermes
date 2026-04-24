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
@Table("telegram_user")
public class TelegramUserEntity {

  @Id
  private Long id;

  @Column("telegram_user_id")
  private Long telegramUserId;

  @Column("username")
  private String username;

  @Column("first_name")
  private String firstName;

  @Column("last_name")
  private String lastName;

  @Column("language_code")
  private String languageCode;
}