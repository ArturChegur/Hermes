package chegur.hermes.frontend.repository;

import chegur.hermes.frontend.dto.ChatStatsView;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface ChatStatsViewRepository extends CrudRepository<ChatStatsView, Long> {

  Optional<ChatStatsView> findByTelegramChatId(Long telegramChatId);
}