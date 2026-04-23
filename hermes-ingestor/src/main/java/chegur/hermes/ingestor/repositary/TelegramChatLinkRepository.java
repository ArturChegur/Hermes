package chegur.hermes.ingestor.repositary;

import chegur.hermes.ingestor.model.TelegramChatLinkEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TelegramChatLinkRepository extends CrudRepository<TelegramChatLinkEntity, Long> {

  @Query("""
    select id, code, chat_id, created_at, expires_at
    from telegram_chat_link_token
    where chat_id = :chatId
      and expires_at > :now
    order by expires_at desc
    limit 1
    """)
  Optional<TelegramChatLinkEntity> findActiveByChatId(@Param("chatId") Long chatId, @Param("now") LocalDateTime now);
}