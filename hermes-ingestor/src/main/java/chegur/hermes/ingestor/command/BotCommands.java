package chegur.hermes.ingestor.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum BotCommands {
  GET_LINK("getlink", "Сгенерировать ссылку на статистику чата");

  String value;

  String description;
}