package net.dathoang.lightweightcqrs.commandbus.impl.bus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;
import net.dathoang.lightweightcqrs.commandbus.interfaces.Command;
import net.dathoang.lightweightcqrs.commandbus.interfaces.Middleware;
import net.dathoang.lightweightcqrs.commandbus.interfaces.PipelineContextContainer;
import net.dathoang.lightweightcqrs.commandbus.models.ResultAndExceptionHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DefaultPipelineContextContainerTest {
  @Nested
  @DisplayName("getMiddlewareData() & getMiddlewareData()")
  class GetSetMiddlewareDataTest {
    private static final String TEST_KEY = "test_key";
    private static final String TEST_VALUE = "test_value";

    @Test
    @DisplayName("should get and set middleware data successfully")
    void shouldSetMiddlewareDataSuccessfully() {
      // Arrange
      DefaultPipelineContextContainer contextContainer = new DefaultPipelineContextContainer();

      // Act
      contextContainer.setMiddlewareData(DummyMiddleware.class, TEST_KEY, TEST_VALUE);
      Object setData = contextContainer.getMiddlewareData(DummyMiddleware.class, TEST_KEY);

      // Assert
      assertThat(setData)
          .isEqualTo(TEST_VALUE);
    }
  }

  @Nested
  @DisplayName("bindContext() & resolveContext()")
  class BindResolveContextTest {
    private final DummyContext dummyContext = new DummyContext();

    @Test
    @DisplayName("should bind and resolve context successfully")
    void shouldBindAndResolveContextSuccessfully() {
      // Arrange
      PipelineContextContainer contextContainer = new DefaultPipelineContextContainer();

      // Act
      contextContainer.bindContext(DummyContextInterface.class, dummyContext);
      DummyContextInterface bindedContext = contextContainer.resolveContext(DummyContextInterface.class);

      // Assert
      assertThat(bindedContext)
          .isEqualTo(dummyContext);
    }
  }
}

class DummyMiddleware implements Middleware {

  @Override
  public <R> void preHandle(Command<R> command,
      ResultAndExceptionHolder<R> resultAndExceptionHolder) {

  }

  @Override
  public <R> void postHandle(Command<R> command,
      ResultAndExceptionHolder<R> resultAndExceptionHolder) {

  }
}

interface DummyContextInterface {}

class DummyContext implements DummyContextInterface {}

