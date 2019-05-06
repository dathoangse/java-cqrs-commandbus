package net.dathoang.cqrs.commandbus.message;

import static java.util.Arrays.asList;
import static net.dathoang.cqrs.commandbus.message.ReflectionUtils.getDeclaredFieldValue;
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
import net.dathoang.cqrs.commandbus.middleware.Middleware;
import net.dathoang.cqrs.commandbus.middleware.MiddlewareContext;
import net.dathoang.cqrs.commandbus.middleware.PipelineContextContainer;
import net.dathoang.cqrs.commandbus.middleware.ResultAndExceptionHolder;
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
      middleware1 = mock(Middleware.class);
      middleware2 = mock(Middleware.class);
      middleware3 = mock(Middleware.class);
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
    void shouldShortCircuitAndRaiseTheExceptionWhenMiddlewareRaiseException() {
      Exception exceptionToRaise = new Exception();

      // Arrange
      doAnswer(invocation -> {
        ResultAndExceptionHolder resultAndExceptionHolder =
            (ResultAndExceptionHolder) invocation.getArguments()[1];
        resultAndExceptionHolder.setException(exceptionToRaise);
        return null;
      }).when(middleware2).preHandle(eq(dummyMessage), any());

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
        ResultAndExceptionHolder resultAndExceptionHolder =
            (ResultAndExceptionHolder)invocation.getArguments()[1];
        setResultToResultHolder(resultToRaise, resultAndExceptionHolder);
        return null;
      }).when(middleware2).preHandle(eq(dummyMessage), any());

      // Act
      Object realResult = messageBus.dispatch(dummyMessage);

      // Assert
      verifyMiddlewareCallOnce(asList(middleware1, middleware2), dummyMessage);
      verifyMiddlewareNotCalled(middleware3);
      assertThat(realResult).as("Real result")
          .isEqualTo(resultToRaise);
    }

    @SuppressWarnings("unchecked")
    private void setResultToResultHolder(Object resultToRaise,
        ResultAndExceptionHolder resultAndExceptionHolder) {
      resultAndExceptionHolder.setResult(resultToRaise);
    }

    @Test
    @DisplayName("should not short-circuit or raise exception when there is unexpected exception "
        + "in middleware")
    void shouldNotShortCircuitAndRaiseExceptionWhenThereIsUnexpectedException() throws Exception {
      // Arrange
      doThrow(new RuntimeException())
          .when(middleware1).preHandle(any(), any());
      doThrow(new RuntimeException())
          .when(middleware2).preHandle(any(), any());

      // Act
      messageBus.dispatch(dummyMessage);

      // Assert
      verifyMiddlewareCallOnce(asList(middleware1, middleware2, middleware3), dummyMessage);
    }

    @Test
    @DisplayName("should safely bypass unhandled exceptions raised by middleware.postHandle()")
    void shouldSafelyBypassUnhandledExceptionsRaisedByMiddlewarePostHandler() throws Exception {
      // Arrange
      doThrow(new RuntimeException())
          .when(middleware1).postHandle(any(), any());
      doThrow(new RuntimeException())
          .when(middleware2).postHandle(any(), any());
      doThrow(new RuntimeException())
          .when(middleware3).postHandle(any(), any());

      // Act
      messageBus.dispatch(dummyMessage);

      // Assert
      verifyMiddlewareCallOnce(asList(middleware1, middleware2, middleware3), dummyMessage);
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

    @Test
    @DisplayName("should allow middleware to alter message handler's result")
    void shouldAllowMiddlewareToAlterMessageHandlerResult() throws Exception {
      // Arrange
      Object middlewareResult = new Object();
      doAnswer(answer -> {
        setResultToResultHolder(middlewareResult,
            (ResultAndExceptionHolder)answer.getArguments()[1]);
        return null;
      }).when(middleware2).postHandle(eq(dummyMessage), any());

      // Act
      Object messageBusResult = messageBus.dispatch(dummyMessage);

      // Assert
      assertThat(messageBusResult).isEqualTo(middlewareResult)
          .describedAs("Message bus result should be equal to middleware result instead "
              + "of message handler result");
    }

    @Test
    @DisplayName("should allow middleware to catch message handler's exception in the pipeline")
    void shouldAllowMiddlewareToCatchMessageHandlerException() throws Exception {
      // Arrange
      doThrow(new Exception())
          .when(mockMessageHandler).handle(dummyMessage);
      doAnswer(answer -> {
        // Catch exception in the pipeline by setting exception to null in exception holder
        ((ResultAndExceptionHolder)answer.getArguments()[1]).setException(null);
        return null;
      }).when(middleware2).postHandle(eq(dummyMessage), any());

      // Act
      Throwable messageBusException = catchThrowable(() -> messageBus.dispatch(dummyMessage));

      // Assert
      assertThat(messageBusException).isEqualTo(null)
          .describedAs("Message bus should not throw exception");
    }

    @Test
    @DisplayName("should inject middleware context successfully into inner middlewares and "
        + "message handler")
    void shouldInjectMiddlewareContextSuccessfullyIntoInnerMiddlewaresAndMessageHandler()
        throws Exception {
      // Arrange
      MiddlewareContextA contextAToInject = new MiddlewareContextA();
      MiddlewareContextB contextBToInject = new MiddlewareContextB();
      MiddlewareA middlewareA = spy(new MiddlewareA(contextAToInject));
      MiddlewareB middlewareB = spy(new MiddlewareB(contextBToInject));
      MiddlewareC middlewareC = spy(new MiddlewareC());
      DummyInjectedMessageHandler messageHandler = spy(new DummyInjectedMessageHandler());
      DummyMessage dummyMessage = new DummyMessage();
      MessageHandlerFactory messageHandlerFactory = mock(MessageHandlerFactory.class);
      doReturn(messageHandler)
          .when(messageHandlerFactory).createHandler(dummyMessage.getClass().getName());
      messageBus = new DefaultMessageBus(messageHandlerFactory, asList(
          middlewareA,
          middlewareB,
          middlewareC
      ));

      // Act
      messageBus.dispatch(new DummyMessage());

      // Assert
      assertThat(getDeclaredFieldValue(middlewareA, "contextContainer"))
          .isNotNull();

      assertThat(getDeclaredFieldValue(middlewareB, "contextContainer"))
          .isNotNull();
      assertThat(getDeclaredFieldValue(middlewareB, "contextA"))
          .isEqualTo(contextAToInject);

      assertThat(getDeclaredFieldValue(middlewareC, "contextA"))
          .isEqualTo(contextAToInject);
      assertThat(getDeclaredFieldValue(middlewareC, "contextB"))
          .isEqualTo(contextBToInject);
      verify(middlewareC, times(1))
          .setUpDependency(contextAToInject, contextBToInject);

      assertThat(getDeclaredFieldValue(messageHandler, "contextA"))
          .isEqualTo(contextAToInject);
      assertThat(getDeclaredFieldValue(messageHandler, "contextB"))
          .isEqualTo(contextBToInject);
      verify(messageHandler, times(1))
          .setUpDependency(contextAToInject, contextBToInject);
    }

    private void verifyMiddlewareCallOnce(Middleware middleware, DummyMessage message) {
      verify(middleware, times(1)).preHandle(eq(message), any());
      verify(middleware, times(1)).postHandle(eq(message), any());
    }

    private void verifyMiddlewareCallOnce(List<Middleware> middlewares, DummyMessage message) {
      middlewares.forEach(middleware -> verifyMiddlewareCallOnce(middleware, message));
    }

    private void verifyMiddlewareNotCalled(Middleware middleware) {
      verify(middleware, times(0)).preHandle(any(), any());
      verify(middleware, times(0)).postHandle(any(), any());
    }
  }

  // region Dummy classes
  static class DummyMessage implements Message<Object> {}

  abstract class DummyMessageHandler implements MessageHandler<DummyMessage, Object> { }

  static class MiddlewareA implements Middleware {
    @MiddlewareContext
    private PipelineContextContainer contextContainer;
    private MiddlewareContextA contextAToInject;

    MiddlewareA(
        MiddlewareContextA contextAToInject) {
      this.contextAToInject = contextAToInject;
    }

    @Override
    public <R> void preHandle(Message<R> message,
        ResultAndExceptionHolder<R> resultAndExceptionHolder) {
      contextContainer.bindContext(MiddlewareContextA.class, contextAToInject);
    }

    @Override
    public <R> void postHandle(Message<R> message,
        ResultAndExceptionHolder<R> resultAndExceptionHolder) {
    }
  }

  static class MiddlewareB implements Middleware {
    @MiddlewareContext
    private PipelineContextContainer contextContainer;
    @MiddlewareContext
    private MiddlewareContextA contextA;
    private MiddlewareContextB contextBToInject;

    MiddlewareB(MiddlewareContextB contextBToInject) {
      this.contextBToInject = contextBToInject;
    }

    @Override
    public <R> void preHandle(Message<R> message,
        ResultAndExceptionHolder<R> resultAndExceptionHolder) {
      contextContainer.bindContext(MiddlewareContextB.class, contextBToInject);
    }

    @Override
    public <R> void postHandle(Message<R> message,
        ResultAndExceptionHolder<R> resultAndExceptionHolder) {
    }
  }

  static class MiddlewareC implements Middleware {
    @MiddlewareContext
    private MiddlewareContextA contextA;
    @MiddlewareContext
    private MiddlewareContextB contextB;

    @MiddlewareContext
    protected void setUpDependency(MiddlewareContextA contextA, MiddlewareContextB contextB) {

    }

    @Override
    public <R> void preHandle(Message<R> message,
        ResultAndExceptionHolder<R> resultAndExceptionHolder) {
    }

    @Override
    public <R> void postHandle(Message<R> message,
        ResultAndExceptionHolder<R> resultAndExceptionHolder) {
    }
  }

  static class DummyInjectedMessageHandler implements MessageHandler<DummyMessage, Object> {
    @MiddlewareContext
    private MiddlewareContextA contextA;
    @MiddlewareContext
    private MiddlewareContextB contextB;

    @MiddlewareContext
    protected void setUpDependency(MiddlewareContextA contextA, MiddlewareContextB contextB) {

    }

    @Override
    public Void handle(DummyMessage message) throws Exception {
      return null;
    }
  }

  private class MiddlewareContextA {}

  private class MiddlewareContextB {}
  // endregion
}
