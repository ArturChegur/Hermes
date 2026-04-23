package chegur.hermes.ingestor.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("telegram.bot")
public class TelegramBotProperties {

  String token;

  String username;
}
