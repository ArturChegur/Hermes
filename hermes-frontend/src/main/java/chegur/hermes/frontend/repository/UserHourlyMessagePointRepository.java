package chegur.hermes.frontend.repository;

import chegur.hermes.frontend.dto.UserHourlyMessagePoint;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface UserHourlyMessagePointRepository extends Repository<UserHourlyMessagePoint, LocalDateTime> {

  @Query("""
    with series as (
        select generate_series(
                       date_trunc('hour', now() - interval '7 days'),
                       date_trunc('hour', now()),
                       interval '1 hour'
               ) as hour_start
    ),
    chat_hourly as (
        select src.hour_start,
               sum(src.messages_count)::BIGINT as messages_count
        from v_chat_user_hourly_messages src
        where src.telegram_chat_id = :telegramChatId
        group by src.hour_start
    )
    select series.hour_start,
           coalesce(chat_hourly.messages_count, 0) as messages_count
    from series
             left join chat_hourly on chat_hourly.hour_start = series.hour_start
    order by series.hour_start
    """)
  List<UserHourlyMessagePoint> findChatHourlyForWeek(@Param("telegramChatId") Long telegramChatId);

  @Query("""
    with series as (
        select generate_series(
                       date_trunc('hour', now() - interval '7 days'),
                       date_trunc('hour', now()),
                       interval '1 hour'
               ) as hour_start
    )
    select series.hour_start,
           coalesce(src.messages_count, 0) as messages_count
    from series
             left join v_chat_user_hourly_messages src
                       on src.telegram_chat_id = :telegramChatId
                         and src.telegram_user_id = :telegramUserId
                         and src.hour_start = series.hour_start
    order by series.hour_start
    """)
  List<UserHourlyMessagePoint> findHourlyForWeek(@Param("telegramChatId") Long telegramChatId,
                                                 @Param("telegramUserId") Long telegramUserId);
}