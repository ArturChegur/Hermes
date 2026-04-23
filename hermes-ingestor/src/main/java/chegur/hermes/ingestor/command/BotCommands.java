package chegur.hermes.ingestor.command;

import lombok.Getter;

@Getter
public enum BotCommands {
  GET_LINK("getlink", "Сгенерировать ссылку на статистику чата");

  private final String value;

  private final String description;

  BotCommands(String value, String description) {
    this.value = value;
    this.description = description;
  }
}
