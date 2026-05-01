package chegur.hermes.ingestor.service;

import chegur.hermes.ingestor.properties.OllamaProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaMessageCommentService {

  private static final String OLLAMA_GENERATE_PATH = "/api/generate";

  private final HttpClient httpClient = HttpClient.newHttpClient();

  private final OllamaProperties ollamaProperties;

  private final ObjectMapper objectMapper;

  public String generateComment(String messageText) {
    if (!StringUtils.hasText(messageText)) {
      throw new IllegalStateException("Message text for comment is empty");
    }

    String requestBody = mapRequestBody(
      new OllamaGenerateRequest(ollamaProperties.getModel(), messageText.trim(), false)
    );

    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(normalizeBaseUrl(ollamaProperties.getBaseUrl()) + OLLAMA_GENERATE_PATH))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .build();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (!isSuccessStatusCode(response.statusCode())) {
        log.error("Failed to call ollama. statusCode = {}, body = {}", response.statusCode(), response.body());
        throw new IllegalStateException("Failed to call ollama");
      }

      OllamaGenerateResponse mappedResponse = objectMapper.readValue(response.body(), OllamaGenerateResponse.class);
      if (mappedResponse == null || !StringUtils.hasText(mappedResponse.response())) {
        throw new IllegalStateException("Empty ollama response");
      }

      return mappedResponse.response().trim();
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while calling ollama", ex);
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to call ollama", ex);
    }
  }

  private String mapRequestBody(OllamaGenerateRequest request) {
    try {
      return objectMapper.writeValueAsString(request);
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Failed to map ollama request body", ex);
    }
  }

  private String normalizeBaseUrl(String baseUrl) {
    if (!StringUtils.hasText(baseUrl)) {
      throw new IllegalStateException("Ollama base url is empty");
    }

    if (baseUrl.endsWith("/")) {
      return baseUrl.substring(0, baseUrl.length() - 1);
    }

    return baseUrl;
  }

  private boolean isSuccessStatusCode(int statusCode) {
    return statusCode >= 200 && statusCode < 300;
  }

  private record OllamaGenerateRequest(String model, String prompt, boolean stream) {

  }

  private record OllamaGenerateResponse(String response) {

  }
}