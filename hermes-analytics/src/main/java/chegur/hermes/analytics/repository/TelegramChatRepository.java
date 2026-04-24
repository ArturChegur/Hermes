package chegur.hermes.analytics.repository;

import chegur.hermes.analytics.model.TelegramChatEntity;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface TelegramChatRepository extends CrudRepository<TelegramChatEntity, Long> {

  Optional<TelegramChatEntity> findByTelegramChatId(Long telegramChatId);
}