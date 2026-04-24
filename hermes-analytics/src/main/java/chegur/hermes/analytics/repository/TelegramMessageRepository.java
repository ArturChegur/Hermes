package chegur.hermes.analytics.repository;

import chegur.hermes.analytics.model.TelegramMessageEntity;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface TelegramMessageRepository extends CrudRepository<TelegramMessageEntity, Long> {

  Optional<TelegramMessageEntity> findByTelegramUpdateId(Long telegramUpdateId);

  Optional<TelegramMessageEntity> findByChatRefAndTelegramMessageId(Long chatRef, Integer telegramMessageId);
}