package chegur.hermes.ingestor.command;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

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

  public static Optional<BotCommands> getByName(String name) {
    return EnumSet.allOf(BotCommands.class).stream()
      .filter(enumElement -> Objects.equals(enumElement.getValue(), name))
      .findFirst();
  }
}