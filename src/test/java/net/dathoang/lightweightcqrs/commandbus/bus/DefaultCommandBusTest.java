package net.dathoang.lightweightcqrs.commandbus.bus;

import net.dathoang.lightweightcqrs.commandbus.exceptions.NoCommandHandlerFoundException;
import net.dathoang.lightweightcqrs.commandbus.interfaces.*;
import net.dathoang.lightweightcqrs.commandbus.models.ExceptionHolder;
import net.dathoang.lightweightcqrs.commandbus.models.ResultHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

class DefaultCommandBusTest {
  @Nested
  @DisplayName("dispatch()")
  class Dispatch {
    private DefaultCommandBus commandBus;
    private CommandHandlerFactory handlerFactoryMock;
    private DummyCommand dummyCommand;
    private DummyCommandHandler mockCommandHandler;
    private Object dummyCommandHandlerResult = new Object();
    private Middleware middleware1;
    private Middleware middleware2;
    private Middleware middleware3;

    @BeforeEach
    void setUp() throws Exception {
      // Arrange
      commandBus = new DefaultCommandBus();
      handlerFactoryMock = mock(CommandHandlerFactory.class);
      dummyCommand = new DummyCommand();
      mockCommandHandler = mock(DummyCommandHandler.class);
      middleware1 = mock(Middleware.class);
      middleware2 = mock(Middleware.class);
      middleware3 = mock(Middleware.class);
      commandBus.setHandlerFactory(handlerFactoryMock);
      commandBus.getMiddlewarePipeline().addAll(asList(
          middleware1,
          middleware2,
          middleware3
      ));

      // Mock
      doReturn(mockCommandHandler)
          .when(handlerFactoryMock).createHandler(dummyCommand.getClass().getName());
      doReturn(dummyCommandHandlerResult)
          .when(mockCommandHandler).handle(dummyCommand);
    }

    @Test
    @DisplayName("should call all middlewares when there is no middleware exception")
    void shouldCallAllMiddlewareWhenNoMiddlewareRaiseException() throws Exception {
      // Act
      commandBus.dispatch(dummyCommand);

      // Assert
      verifyMiddlewareCallOnce(asList(middleware1, middleware2, middleware3), dummyCommand);
    }

    @Test
    @DisplayName("should short-circuit and raise exception when middleware raise exception")
    void shouldShortCircuitAndRaiseTheExceptionWhenMiddlewareRaiseException() {
      Exception exceptionToRaise = new Exception();

      // Arrange
      doAnswer(invocation -> {
        ExceptionHolder exceptionHolder = (ExceptionHolder)invocation.getArguments()[2];
        exceptionHolder.setException(exceptionToRaise);
        return null;
      }).when(middleware2).preHandle(eq(dummyCommand), any(), any());

      // Act
      Throwable commandBusException = catchThrowable(() -> commandBus.dispatch(dummyCommand));

      // Assert
      verifyMiddlewareCallOnce(asList(middleware1, middleware2), dummyCommand);
      verifyMiddlewareNotCalled(middleware3);
      assertThat(commandBusException)
          .as("The exception thrown outside of command bus should be the exception raised by middleware")
          .isEqualTo(exceptionToRaise);
    }

    @Test
    @DisplayName("should short-circuit and return result when middleware raise result")
    void shouldShortCircuitAndReturnResultWhenMiddlewareRaiseResult() throws Exception {
      Object resultToRaise = new Object();

      // Arrange
      doAnswer(invocation -> {
        ResultHolder resultHolder = (ResultHolder)invocation.getArguments()[1];
        resultHolder.setResult(resultToRaise);
        return null;
      }).when(middleware2).preHandle(eq(dummyCommand), any(), any());

      // Act
      Object realResult = commandBus.dispatch(dummyCommand);

      // Assert
      verifyMiddlewareCallOnce(asList(middleware1, middleware2), dummyCommand);
      verifyMiddlewareNotCalled(middleware3);
      assertThat(realResult).as("Real result")
          .isEqualTo(resultToRaise);
    }

    @Test
    @DisplayName("should not short-circuit or raise exception when there is unexpected exception in middleware")
    void shouldNotShortCircuitAndRaiseExceptionWhenThereIsUnexpectedException() throws Exception {
      // Arrange
      doThrow(new RuntimeException())
          .when(middleware1).preHandle(any(), any(), any());
      doThrow(new RuntimeException())
          .when(middleware2).preHandle(any(), any(), any());

      // Act
      commandBus.dispatch(dummyCommand);

      // Assert
      verifyMiddlewareCallOnce(asList(middleware1, middleware2, middleware3), dummyCommand);
    }

    @Test
    @DisplayName("should throw NoCommandHandlerFoundException when command handler factory can't create handler for the requested command")
    void shouldThrowExceptionWhenCommandHandlerFactoryCantCreateCommand() {
      // Arrange
      doReturn(null)
          .when(handlerFactoryMock).createHandler(dummyCommand.getClass().getName());

      // Act
      Throwable commandBusException = catchThrowable(() -> commandBus.dispatch(dummyCommand));

      // Assert
      assertThat(commandBusException).isInstanceOf(NoCommandHandlerFoundException.class)
          .describedAs("Thrown exception should be NoCommandHandlerFoundException");
    }

    @Test
    @DisplayName("should call command handler when there is no short-circuit")
    void shouldCallCommandHandlerWhenThereIsNoShortCircuit() throws Exception {
      // Act
      Object commandBusResult = commandBus.dispatch(dummyCommand);

      // Assert
      verify(mockCommandHandler, times(1)).handle(dummyCommand);
      assertThat(commandBusResult).isEqualTo(dummyCommandHandlerResult)
          .describedAs("Command bus result should be equals to command handler result");
    }

    @Test
    @DisplayName("should allow middleware to alter command handler's result")
    void shouldAllowMiddlewareToAlterCommandHandlerResult() throws Exception {
      // Arrange
      Object middlewareResult = new Object();
      doAnswer(answer -> {
        ((ResultHolder)answer.getArguments()[1]).setResult(middlewareResult);
        return null;
      }).when(middleware2).postHandle(eq(dummyCommand), any(), any());

      // Act
      Object commandBusResult = commandBus.dispatch(dummyCommand);

      // Assert
      assertThat(commandBusResult).isEqualTo(middlewareResult)
          .describedAs("Command bus result should be equal to middleware result instead of command handler result");
    }

    @Test
    @DisplayName("should allow middleware to catch command handler's exception in the pipeline")
    void shouldAllowMiddlewareToCatchCommandHandlerException() throws Exception {
      // Arrange
      doThrow(new Exception())
          .when(mockCommandHandler).handle(dummyCommand);
      doAnswer(answer -> {
        // Catch exception in the pipeline by setting exception to null in exception holder
        ((ExceptionHolder)answer.getArguments()[2]).setException(null);
        return null;
      }).when(middleware2).postHandle(eq(dummyCommand), any(), any());

      // Act
      Throwable commandBusException = catchThrowable(() -> commandBus.dispatch(dummyCommand));

      // Assert
      assertThat(commandBusException).isEqualTo(null)
          .describedAs("Command bus should not throw exception");
    }

    private void verifyMiddlewareCallOnce(Middleware middleware, DummyCommand command) {
      verify(middleware, times(1))
          .preHandle(eq(command), any(), any());
      verify(middleware, times(1))
          .postHandle(eq(command), any(), any());
    }

    private void verifyMiddlewareCallOnce(List<Middleware> middlewares, DummyCommand command) {
      middlewares.forEach(middleware -> verifyMiddlewareCallOnce(middleware, command));
    }

    private void verifyMiddlewareNotCalled(Middleware middleware) {
      verify(middleware, times(0))
          .preHandle(any(), any(), any());
      verify(middleware, times(0))
          .postHandle(any(), any(), any());
    }
  }
}

class DummyCommand implements Command<Object> {}

abstract class DummyCommandHandler implements CommandHandler<DummyCommand, Object> { }
