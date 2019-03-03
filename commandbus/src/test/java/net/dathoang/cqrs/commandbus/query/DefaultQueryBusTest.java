package net.dathoang.cqrs.commandbus.query;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import net.dathoang.cqrs.commandbus.middleware.Middleware;
import net.dathoang.cqrs.commandbus.query.DefaultQueryBus;
import net.dathoang.cqrs.commandbus.query.Query;
import net.dathoang.cqrs.commandbus.query.QueryBus;
import net.dathoang.cqrs.commandbus.query.QueryHandler;
import net.dathoang.cqrs.commandbus.query.QueryHandlerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DefaultQueryBusTest {
  @Nested
  @DisplayName("dispatch()")
  class DispatchTest {
    @Test
    @DisplayName("should dispatch through middleware pipeline and to query handler and return result successfully")
    void shouldDispatchThroughMiddlewarePipelineAndToQueryHandlerSuccessfully() throws Exception {
      // Arrange
      DummyQuery dummyQuery = mock(DummyQuery.class);
      Object handlerResult = new Object();
      DummyQueryHandler dummyQueryHandler = mock(DummyQueryHandler.class);
      doReturn(handlerResult)
          .when(dummyQueryHandler).handle(dummyQuery);
      QueryHandlerFactory queryHandlerFactory = mock(QueryHandlerFactory.class);
      doReturn(dummyQueryHandler)
          .when(queryHandlerFactory).createHandler(dummyQuery.getClass().getName());
      List<Middleware> middlewareList = asList(
          mock(Middleware.class),
          mock(Middleware.class),
          mock(Middleware.class)
      );
      QueryBus queryBus = new DefaultQueryBus(queryHandlerFactory, middlewareList);

      // Act
      Object queryBusResult = queryBus.dispatch(dummyQuery);

      // Assert
      middlewareList.forEach(middleware -> {
        verify(middleware, times(1))
            .preHandle(eq(dummyQuery), any());
        verify(middleware, times(1))
            .postHandle(eq(dummyQuery), any());
      });
      verify(dummyQueryHandler, times(1))
          .handle(dummyQuery);
      assertThat(queryBusResult)
          .isEqualTo(handlerResult)
          .describedAs("Result returned from query bus must be same as result returned from query handler");
    }
  }

  // region Dummy classes for test
  class DummyQuery implements Query<Object> {}

  abstract class DummyQueryHandler implements
      QueryHandler<DummyQuery, Object> {}
}
