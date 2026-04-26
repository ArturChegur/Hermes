package chegur.hermes.frontend.controller;

import chegur.hermes.frontend.dto.ChatPageDataDto;
import chegur.hermes.frontend.dto.UserPageDataDto;
import chegur.hermes.frontend.service.ChatStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class StatsPageController {

  private static final String TELEGRAM_BOT_USERNAME_PROPERTY = "${TELEGRAM_BOT_USERNAME:HermesBot}";

  private static final String DEFAULT_BOT_USERNAME = "UNNAMED_BOT";

  private final ChatStatisticsService statisticsService;

  @Value(TELEGRAM_BOT_USERNAME_PROPERTY)
  private String botUsername;

  @ModelAttribute("botUsername")
  public String botUsername() {
    return normalizeBotUsername(botUsername);
  }

  @GetMapping("/{chatLink:[A-Za-z0-9_-]+}")
  public String chatStats(@PathVariable String chatLink, Model model) {
    return statisticsService.getChatPage(chatLink)
      .map(data -> {
        fillCommonModel(model, data, null);
        return "index";
      })
      .orElse("error/404");
  }

  @GetMapping("/{chatLink:[A-Za-z0-9_-]+}/{userId:\\d+}")
  public String userStats(@PathVariable String chatLink, @PathVariable Long userId, Model model) {
    return statisticsService.getUserPage(chatLink, userId)
      .map(data -> {
        fillCommonModel(model, data.getChatPage(), data);
        return "user";
      })
      .orElse("error/404");
  }

  private void fillCommonModel(Model model, ChatPageDataDto chatData, UserPageDataDto userData) {
    model.addAttribute("chatLink", chatData.getLink().getCode());
    model.addAttribute("linkCreatedAt", chatData.getLink().getCreatedAt());
    model.addAttribute("chatStats", chatData.getChatStats());
    model.addAttribute("participants", chatData.getParticipants());
    model.addAttribute("chatHourlyPoints", chatData.getHourlyPoints());

    if (userData != null) {
      model.addAttribute("userStats", userData.getUserStats());
      model.addAttribute("hourlyPoints", userData.getHourlyPoints());
    } else {
      model.addAttribute("userStats", null);
      model.addAttribute("hourlyPoints", null);
    }
  }

  private String normalizeBotUsername(String username) {
    if (username == null || username.isBlank()) {
      return DEFAULT_BOT_USERNAME;
    }

    String trimmed = username.trim();
    return trimmed.startsWith("@") ? trimmed : "@" + trimmed;
  }
}