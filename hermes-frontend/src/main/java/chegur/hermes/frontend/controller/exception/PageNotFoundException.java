package chegur.hermes.frontend.controller.exception;

public class PageNotFoundException extends RuntimeException {

  public PageNotFoundException(String message) {
    super(message);
  }
}