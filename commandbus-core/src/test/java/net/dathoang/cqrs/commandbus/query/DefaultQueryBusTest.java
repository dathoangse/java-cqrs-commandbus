package net.dathoang.cqrs.commandbus.query;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import net.dathoang.cqrs.commandbus.message.Message;
import net.dathoang.cqrs.commandbus.middleware.Middleware;
import net.dathoang.cqrs.commandbus.middleware.NextMiddlewareFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DefaultQueryBusTest {
  @Nested
  @DisplayName("dispatch()")
  static class DispatchTest {
    @Test
    @DisplayName("should dispatch through middleware pipeline and to query handler and return "
        + "result successfully")
    void shouldDispatchThroughMiddlewarePipelineAndToQueryHandlerSuccessfully() throws Exception {
      // Arrange
      DummyQuery dummyQuery = mock(DummyQuery.class);
      Object handlerResult = new Object();
      DummyQueryHandler dummyQueryHandler = mock(DummyQueryHandler.class);
      doReturn(handlerResult)
          .when(dummyQueryHandler).handle(dummyQuery);
      QueryHandlerFactory queryHandlerFactory = mock(QueryHandlerFactory.class);
      doReturn(dummyQueryHandler)
          .when(queryHandlerFactory).createQueryHandler(dummyQuery.getClass().getName());
      List<Middleware> middlewareList = asList(
          spy(new DummyMiddleware()),
          spy(new DummyMiddleware()),
          spy(new DummyMiddleware())
      );
      QueryBus queryBus = new DefaultQueryBus(queryHandlerFactory, middlewareList);

      // Act
      Object queryBusResult = queryBus.dispatch(dummyQuery);

      // Assert
      for (Middleware middleware : middlewareList) {
        verify(middleware, times(1))
            .handle(eq(dummyQuery), any());
      }
      verify(dummyQueryHandler, times(1))
          .handle(dummyQuery);
      assertThat(queryBusResult)
          .isEqualTo(handlerResult)
          .describedAs("Result returned from query bus must be same as result returned "
              + "from query handler");
    }
  }

  // region Dummy classes for test
  class DummyQuery implements Query<Object> {}

  abstract class DummyQueryHandler implements
      QueryHandler<DummyQuery, Object> { }

  static class DummyMiddleware implements Middleware {
    @Override
    public <R> R handle(Message<R> message, NextMiddlewareFunction<Message<R>, R> next) throws Exception {
      return next.call(message);
    }
  }
}
