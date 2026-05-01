package chegur.hermes.ingestor.dispatcher;

import chegur.hermes.ingestor.command.BotCommands;
import chegur.hermes.ingestor.service.OllamaMessageCommentService;
import chegur.hermes.ingestor.service.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotCommandCommentMessageHandler implements BotCommandHandler {

  private static final String REPLY_REQUIRED_TEXT = "Команда должна быть вызвана ответом на сообщение";

  private static final String COMMENT_ERROR_TEXT = "Не удалось получить комментарий. Попробуйте позже";

  private final OllamaMessageCommentService ollamaMessageCommentService;

  private final TelegramMessageService telegramMessageService;

  @Override
  public void handle(Update update) {
    Message commandMessage = update.getMessage();
    Long chatId = commandMessage.getChatId();
    Message repliedMessage = commandMessage.getReplyToMessage();

    String messageToComment = extractMessageToComment(repliedMessage);

    if (!StringUtils.hasText(messageToComment)) {
      telegramMessageService.sendMessage(chatId, REPLY_REQUIRED_TEXT);
      return;
    }

    Integer repliedMessageId = repliedMessage.getMessageId();

    try {
      String llmComment = ollamaMessageCommentService.generateComment(messageToComment);
      telegramMessageService.sendMessage(chatId, llmComment, repliedMessageId);
    } catch (IllegalStateException ex) {
      log.warn("Failed to generate llm comment for chatId = {}", chatId, ex);
      telegramMessageService.sendMessage(chatId, COMMENT_ERROR_TEXT, repliedMessageId);
    }
  }

  @Override
  public BotCommands getCommand() {
    return BotCommands.COMMENT;
  }

  private String extractMessageToComment(Message repliedMessage) {
    if (repliedMessage == null) {
      return null;
    }

    if (StringUtils.hasText(repliedMessage.getText())) {
      return repliedMessage.getText();
    }

    if (StringUtils.hasText(repliedMessage.getCaption())) {
      return repliedMessage.getCaption();
    }

    return null;
  }
}
