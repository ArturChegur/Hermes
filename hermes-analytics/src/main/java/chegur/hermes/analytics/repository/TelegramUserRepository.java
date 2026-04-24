package chegur.hermes.analytics.repository;

import chegur.hermes.analytics.model.TelegramUserEntity;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface TelegramUserRepository extends CrudRepository<TelegramUserEntity, Long> {

  Optional<TelegramUserEntity> findByTelegramUserId(Long telegramUserId);
}