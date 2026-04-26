package chegur.hermes.frontend.repository;

import chegur.hermes.frontend.model.view.ChatParticipantStatsView;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ChatParticipantStatsRepository extends CrudRepository<ChatParticipantStatsView, Long> {

  @Query("""
    select user_ref,
           chat_ref,
           telegram_chat_id,
           telegram_user_id,
           display_name,
           total_messages,
           messages_last_24_hours,
           messages_last_1_hour
    from v_chat_user_stats
    where telegram_chat_id = :telegramChatId
    order by total_messages desc, telegram_user_id asc
    """)
  List<ChatParticipantStatsView> findByTelegramChatId(@Param("telegramChatId") Long telegramChatId);

  @Query("""
    select user_ref,
           chat_ref,
           telegram_chat_id,
           telegram_user_id,
           display_name,
           total_messages,
           messages_last_24_hours,
           messages_last_1_hour
    from v_chat_user_stats
    where telegram_chat_id = :telegramChatId
      and telegram_user_id = :telegramUserId
    """)
  Optional<ChatParticipantStatsView> findByTelegramChatIdAndTelegramUserId(@Param("telegramChatId") Long telegramChatId,
                                                                           @Param("telegramUserId") Long telegramUserId);
}