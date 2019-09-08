package net.dathoang.cqrs.commandbus.message;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import net.dathoang.cqrs.commandbus.exceptions.NoHandlerFoundException;
import net.dathoang.cqrs.commandbus.middleware.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DefaultMessageBusTest {
  @Nested
  @DisplayName("dispatch()")
  class Dispatch {
    private DefaultMessageBus messageBus;
    private MessageHandlerFactory messageHandlerFactoryMock;
    private DummyMessage dummyMessage;
    private DummyMessageHandler mockMessageHandler;
    private Object dummyMessageHandlerResult = new Object();
    private Middleware middleware1;
    private Middleware middleware2;
    private Middleware middleware3;

    @BeforeEach
    void setUp() throws Exception {
      // Arrange
      messageHandlerFactoryMock = mock(MessageHandlerFactory.class);
      dummyMessage = new DummyMessage();
      mockMessageHandler = mock(DummyMessageHandler.class);
      middleware1 = spy(new DummyMiddleware());
      middleware2 = spy(new DummyMiddleware());
      middleware3 = spy(new DummyMiddleware());
      messageBus = new DefaultMessageBus(messageHandlerFactoryMock, asList(
          middleware1,
          middleware2,
          middleware3
      ));

      // Mock
      doReturn(mockMessageHandler)
          .when(messageHandlerFactoryMock).createHandler(dummyMessage.getClass().getName());
      doReturn(dummyMessageHandlerResult)
          .when(mockMessageHandler).handle(dummyMessage);
    }

    @Test
    @DisplayName("should call all middlewares when there is no middleware exception")
    void shouldCallAllMiddlewareWhenNoMiddlewareRaiseException() throws Exception {
      // Act
      messageBus.dispatch(dummyMessage);

      // Assert
      verifyMiddlewareCallOnce(asList(middleware1, middleware2, middleware3), dummyMessage);
    }

    @Test
    @DisplayName("should short-circuit and raise exception when middleware raise exception")
    void shouldShortCircuitAndRaiseTheExceptionWhenMiddlewareRaiseException() throws Exception {
      Exception exceptionToRaise = new RuntimeException("Exception raised by middleware2");

      // Arrange
      doAnswer(invocation -> {
        throw exceptionToRaise;
      }).when(middleware2).handle(eq(dummyMessage), any());

      // Act
      Throwable messageBusException = catchThrowable(() -> messageBus.dispatch(dummyMessage));

      // Assert
      verifyMiddlewareCallOnce(asList(middleware1, middleware2), dummyMessage);
      verifyMiddlewareNotCalled(middleware3);
      assertThat(messageBusException)
          .as("The exception thrown outside of message bus should be the exception "
              + "raised by middleware")
          .isEqualTo(exceptionToRaise);
    }

    @Test
    @DisplayName("should short-circuit and return result when middleware raise result")
    void shouldShortCircuitAndReturnResultWhenMiddlewareRaiseResult() throws Exception {
      Object resultToRaise = new Object();

      // Arrange
      doAnswer(invocation -> {
        return resultToRaise;
      }).when(middleware2).handle(eq(dummyMessage), any());

      // Act
      Object realResult = messageBus.dispatch(dummyMessage);

      // Assert
      verifyMiddlewareCallOnce(asList(middleware1, middleware2), dummyMessage);
      verifyMiddlewareNotCalled(middleware3);
      assertThat(realResult).as("Real result")
          .isEqualTo(resultToRaise);
    }

    @Test
    @DisplayName("should throw NoHandlerFoundException when message handler factory can't create "
        + "handler for the requested message")
    void shouldThrowExceptionWhenMessageHandlerFactoryCantCreateMessageHandler() {
      // Arrange
      doReturn(null)
          .when(messageHandlerFactoryMock).createHandler(dummyMessage.getClass().getName());

      // Act
      Throwable messageBusException = catchThrowable(() -> messageBus.dispatch(dummyMessage));

      // Assert
      assertThat(messageBusException).isInstanceOf(NoHandlerFoundException.class)
          .describedAs("Thrown exception should be NoHandlerFoundException");
    }

    @Test
    @DisplayName("should call message handler when there is no short-circuit")
    void shouldCallMessageHandlerWhenThereIsNoShortCircuit() throws Exception {
      // Act
      Object messageBusResult = messageBus.dispatch(dummyMessage);

      // Assert
      verify(mockMessageHandler, times(1)).handle(dummyMessage);
      assertThat(messageBusResult).isEqualTo(dummyMessageHandlerResult)
          .describedAs("Message bus result should be equals to message handler result");
    }

    private void verifyMiddlewareCallOnce(Middleware middleware, DummyMessage message) throws Exception {
      verify(middleware, times(1)).handle(eq(message), any());
    }

    private void verifyMiddlewareCallOnce(List<Middleware> middlewares, DummyMessage message) throws Exception {
      for (Middleware middleware : middlewares) {
        verifyMiddlewareCallOnce(middleware, message);
      }
    }

    private void verifyMiddlewareNotCalled(Middleware middleware) throws Exception {
      verify(middleware, times(0)).handle(any(), any());
    }
  }

  // region Dummy classes
  static class DummyMessage implements Message<Object> {}

  static class DummyMiddleware implements Middleware {
    @Override
    public <R> R handle(Message<R> message, NextMiddlewareFunction<Message<R>, R> next) throws Exception {
      return next.call(message);
    }
  }

  abstract class DummyMessageHandler implements MessageHandler<DummyMessage, Object> { }
}
