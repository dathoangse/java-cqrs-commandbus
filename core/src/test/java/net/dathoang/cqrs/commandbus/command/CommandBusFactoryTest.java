package net.dathoang.cqrs.commandbus.command;

import static java.util.Arrays.asList;
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

class CommandBusFactoryTest {
  @Nested
  @DisplayName("create()")
  static class CreateTest {
    @Test
    @DisplayName("should create DefaultCommandBus instance successfully")
    void shouldCreateDefaultCommandBusInstanceSuccessfully() {
      // Arrange
      CommandHandlerFactory commandHandlerFactory = mock(CommandHandlerFactory.class);
      List<Middleware> middlewareList = singletonList(mock(Middleware.class));

      // Act
      CommandBus commandBus = CommandBusFactory.create(commandHandlerFactory, middlewareList);

      // Assert
      assertThat(commandBus)
          .isNotNull()
          .isInstanceOf(DefaultCommandBus.class);
    }
  }
}
