package net.dathoang.cqrs.commandbus.query;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;
import net.dathoang.cqrs.commandbus.middleware.Middleware;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class QueryBusFactoryTest {
  @Nested
  @DisplayName("create()")
  static class CreateTest {
    @Test
    @DisplayName("should create DefaultQueryBus instance successfully")
    void shouldCreateDefaultQueryBusInstanceSuccessfully() {
      // Arrange
      QueryHandlerFactory queryHandlerFactory = mock(QueryHandlerFactory.class);
      List<Middleware> middlewareList = singletonList(mock(Middleware.class));

      // Act
      QueryBus queryBus = QueryBusFactory.create(queryHandlerFactory, middlewareList);

      // Assert
      assertThat(queryBus)
          .isNotNull()
          .isInstanceOf(DefaultQueryBus.class);
    }
  }
}
