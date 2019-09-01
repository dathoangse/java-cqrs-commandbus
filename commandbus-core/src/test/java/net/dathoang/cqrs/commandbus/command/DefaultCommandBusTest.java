package net.dathoang.cqrs.commandbus.command;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import net.dathoang.cqrs.commandbus.message.Message;
import net.dathoang.cqrs.commandbus.middleware.Middleware;
import net.dathoang.cqrs.commandbus.middleware.NextFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DefaultCommandBusTest {
  @Nested
  @DisplayName("dispatch()")
  static class DispatchTest {
    @Test
    @DisplayName("should dispatch through middleware pipeline and to command handler and return "
        + "result successfully")
    void shouldDispatchThroughMiddlewarePipelineAndTocommandHandlerSuccessfully() throws Exception {
      // Arrange
      DummyCommand dummyCommand = mock(DummyCommand.class);
      Object handlerResult = new Object();
      DummyCommandHandler dummyCommandHandler = mock(DummyCommandHandler.class);
      doReturn(handlerResult)
          .when(dummyCommandHandler).handle(dummyCommand);
      CommandHandlerFactory commandHandlerFactory = mock(CommandHandlerFactory.class);
      doReturn(dummyCommandHandler)
          .when(commandHandlerFactory).createCommandHandler(dummyCommand.getClass().getName());
      List<Middleware> middlewareList = asList(
          spy(new DummyMiddleware()),
          spy(new DummyMiddleware()),
          spy(new DummyMiddleware())
      );
      CommandBus commandBus = new DefaultCommandBus(commandHandlerFactory, middlewareList);

      // Act
      Object commandBusResult = commandBus.dispatch(dummyCommand);

      // Assert
      for (Middleware middleware : middlewareList) {
        verify(middleware, times(1))
            .handle(eq(dummyCommand), any());
      }
      verify(dummyCommandHandler, times(1))
          .handle(dummyCommand);
      assertThat(commandBusResult)
          .isEqualTo(handlerResult)
          .describedAs("Result returned from command bus must be same as result returned"
              + "from command handler");
    }
  }

  // region Dummy classes for test
  class DummyCommand implements Command<Object> {}

  abstract class DummyCommandHandler implements CommandHandler<DummyCommand, Object> {}

  static class DummyMiddleware implements Middleware {
    @Override
    public <R> R handle(Message<R> message, NextFunction<Message<R>, R> next) throws Exception {
      return next.call(message);
    }
  }
}
