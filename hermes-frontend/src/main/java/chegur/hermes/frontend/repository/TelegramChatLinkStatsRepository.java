package chegur.hermes.frontend.repository;

import chegur.hermes.frontend.model.entity.ChatLinkDetailsEntity;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TelegramChatLinkStatsRepository extends CrudRepository<ChatLinkDetailsEntity, String> {

  @Query("""
    select l.code,
           c.telegram_chat_id,
           l.created_at,
           l.expires_at
    from telegram_chat_link l
             join telegram_chat c on c.telegram_chat_id = l.chat_id
    where l.code = :code
      and l.expires_at > now()
    order by l.expires_at desc
    limit 1
    """)
  Optional<ChatLinkDetailsEntity> findActiveByCode(@Param("code") String code);
}