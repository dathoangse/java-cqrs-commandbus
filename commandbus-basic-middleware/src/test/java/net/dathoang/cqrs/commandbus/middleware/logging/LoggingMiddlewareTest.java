package net.dathoang.cqrs.commandbus.middleware.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

import net.dathoang.cqrs.commandbus.command.Command;
import net.dathoang.cqrs.commandbus.message.Message;
import net.dathoang.cqrs.commandbus.middleware.ResultAndExceptionHolder;
import net.dathoang.cqrs.commandbus.query.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LoggingMiddlewareTest {
  @Nested
  @DisplayName("preHandle()")
  static class PreHandleTest {
    @ParameterizedTest
    @ValueSource(strings = {"message", "command", "query"})
    @DisplayName("should log successfully")
    void shouldLogSuccessfully(String messageType) {
      // Arrange
      Message<Object> message = createDummyMessage(messageType);
      LoggingMiddleware middleware = new LoggingMiddleware();

      // Act
      Throwable ex = catchThrowable(() ->
          middleware.preHandle(message, new ResultAndExceptionHolder<>()));

      // Assert
      assertThat(ex)
          .describedAs("should not throw exception")
          .isNull();
    }
  }

  @Nested
  @DisplayName("postHandle()")
  static class PostHandleTest {
    @ParameterizedTest
    @ValueSource(strings = {"message", "command", "query"})
    @DisplayName("should log successfully when received exception")
    void shouldLogSuccessfullyWhenReceivedException(String messageType) {
      // Arrange
      Message<Object> message = createDummyMessage(messageType);
      LoggingMiddleware middleware = new LoggingMiddleware();
      ResultAndExceptionHolder<Object> resultAndExceptionHolder = new ResultAndExceptionHolder<>();
      resultAndExceptionHolder.setException(new RuntimeException());

      // Act
      Throwable ex = catchThrowable(() -> middleware.postHandle(message, resultAndExceptionHolder));

      // Assert
      assertThat(ex)
          .describedAs("should not throw exception")
          .isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"message", "command", "query"})
    @DisplayName("should log successfully when received result")
    void shouldLogSuccessfullyWhenReceivedResult(String messageType) {
      // Arrange
      Message<Object> message = createDummyMessage(messageType);
      LoggingMiddleware middleware = new LoggingMiddleware();
      ResultAndExceptionHolder<Object> resultAndExceptionHolder = new ResultAndExceptionHolder<>();
      resultAndExceptionHolder.setResult(new Object());

      // Act
      Throwable ex = catchThrowable(() -> middleware.postHandle(message, resultAndExceptionHolder));

      // Assert
      assertThat(ex)
          .describedAs("should not throw exception")
          .isNull();
    }
  }

  @SuppressWarnings("unchecked")
  private static Message<Object> createDummyMessage(String messageType) {
    switch (messageType) {
      case "command":
        return mock(Command.class);
      case "query":
        return mock(Query.class);
      case "message":
        return mock(Message.class);
      default:
        return null;
    }
  }
}
