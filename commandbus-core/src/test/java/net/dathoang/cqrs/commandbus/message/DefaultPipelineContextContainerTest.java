package net.dathoang.cqrs.commandbus.message;

import static org.assertj.core.api.Assertions.assertThat;

import net.dathoang.cqrs.commandbus.middleware.Middleware;
import net.dathoang.cqrs.commandbus.middleware.PipelineContextContainer;
import net.dathoang.cqrs.commandbus.middleware.ResultAndExceptionHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DefaultPipelineContextContainerTest {
  @Nested
  @DisplayName("getMiddlewareData() & getMiddlewareData()")
  static class GetSetMiddlewareDataTest {
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
  static class BindResolveContextTest {
    private final DummyContext dummyContext = new DummyContext();

    @Test
    @DisplayName("should bind and resolve context successfully")
    void shouldBindAndResolveContextSuccessfully() {
      // Arrange
      PipelineContextContainer contextContainer = new DefaultPipelineContextContainer();

      // Act
      contextContainer.bindContext(DummyContextInterface.class, dummyContext);
      DummyContextInterface bindedContext =
          (DummyContextInterface)contextContainer.resolveContext(DummyContextInterface.class);

      // Assert
      assertThat(bindedContext)
          .isEqualTo(dummyContext);
    }
  }

  // region Dummy classes for testing
  class DummyMiddleware implements Middleware {

    @Override
    public <R> void preHandle(Message<R> message,
        ResultAndExceptionHolder<R> resultAndExceptionHolder) {

    }

    @Override
    public <R> void postHandle(Message<R> message,
        ResultAndExceptionHolder<R> resultAndExceptionHolder) {

    }
  }

  interface DummyContextInterface {}

  static class DummyContext implements DummyContextInterface {}
  // endregion
}

