package net.dathoang.cqrs.commandbus.middleware.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

import net.dathoang.cqrs.commandbus.command.Command;
import net.dathoang.cqrs.commandbus.message.Message;
import net.dathoang.cqrs.commandbus.middleware.NextMiddlewareFunction;
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
          middleware.handle(message, (handlingMessage) -> null));

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
      Exception exceptionRaisedByNextMiddleware = new Exception("Exception raised in next middleware");
      NextMiddlewareFunction<Message<Object>, Object> nextFunc = (handlingMessage) -> {
        throw exceptionRaisedByNextMiddleware;
      };

      // Act
      Throwable realException = catchThrowable(() -> middleware.handle(message, nextFunc));

      // Assert
      assertThat(realException)
          .describedAs("should throw the same exception as the exception raised by next middleware")
          .isEqualTo(exceptionRaisedByNextMiddleware);
    }

    @ParameterizedTest
    @ValueSource(strings = {"message", "command", "query"})
    @DisplayName("should log successfully when received result")
    void shouldLogSuccessfullyWhenReceivedResult(String messageType) throws Exception {
      // Arrange
      Message<Object> message = createDummyMessage(messageType);
      Object dummyResult = new Object();
      LoggingMiddleware middleware = new LoggingMiddleware();
      NextMiddlewareFunction<Message<Object>, Object> nextFunc = (handlingMessage) -> dummyResult;

      // Act
      Object realResult = middleware.handle(message, nextFunc);

      // Assert
      assertThat(realResult)
          .describedAs("should return the same result as the result of next middleware")
          .isEqualTo(dummyResult);
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
