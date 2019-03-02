package net.dathoang.cqrs.commandbus.factory;

import static java.util.Arrays.asList;
import static net.dathoang.cqrs.commandbus.factory.ReflectionUtils.getDeclaredFieldValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import net.dathoang.cqrs.commandbus.middleware.MiddlewareContext;
import net.dathoang.cqrs.commandbus.middleware.PipelineContextContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MiddlewareContextInjectorTest {
  private DummyType dummyContext;
  private PipelineContextContainer contextContainer;
  private static final List<String> annotatedFieldNames = asList(
      "privateAnnotatedFieldA", "protectedAnnotatedFieldA", "publicAnnotatedFieldA",
      "privateAnnotatedFieldB", "protectedAnnotatedFieldB", "publicAnnotatedFieldB");
  private static final List<String> nonContextFieldNames = asList(
      "privateFieldA", "protectedFieldA", "publicFieldA", "publicFieldAWithAnotherAnnotation",
      "privateFieldB", "protectedFieldB", "publicFieldB", "publicFieldBWithAnotherAnnotation");

  @BeforeEach
  void setUp() {
    dummyContext = new DummyType();
    contextContainer = mock(PipelineContextContainer.class);
    when(contextContainer.resolveContext(DummyType.class))
        .thenReturn(dummyContext);
  }

  @Nested
  @DisplayName("injectContext()")
  class InjectContextTest {
    @Test
    @DisplayName("should inject context into all annotated fields")
    void shouldInjectContextIntoAllAnnotatedFields() {
      // Arrange
      DummyClassB injectingObject = new DummyClassB();

      // Act
      MiddlewareContextInjector.injectContext(contextContainer, injectingObject);

      // Assert
      assertThat(getValuesOfFields(injectingObject, annotatedFieldNames))
          .containsExactly(duplicateList(dummyContext, 6).toArray(new DummyType[6]));
    }

    @Test
    @DisplayName("should not inject context into fields that doesn't have @MiddlewareContext annotation")
    void shouldNotInjectContextIntoFieldsThatDoesntHaveMiddlewareContextAnnotation() {
      // Arrange
      DummyType nonAnnotatedFieldValue = new DummyType();
      DummyClassB injectingObject = spy(new DummyClassB(null, nonAnnotatedFieldValue));

      // Act
      MiddlewareContextInjector.injectContext(contextContainer, injectingObject);

      // Assert
      assertThat(getValuesOfFields(injectingObject, nonContextFieldNames))
          .containsExactly(duplicateList(nonAnnotatedFieldValue, 8).toArray(new DummyType[8]));
    }

    @Test
    @DisplayName("should inject context into all annotated methods")
    void shouldInjectContextIntoAllAnnotatedMethods() {
      // Arrange
      DummyClassB injectingObject = spy(new DummyClassB());

      // Act
      MiddlewareContextInjector.injectContext(contextContainer, injectingObject);

      // Assert
      verifyAnnotatedMethodsCalled(injectingObject, dummyContext);
    }

    @Test
    @DisplayName("should not inject context into methods that doesn't have @MiddlewareContext annotation")
    void shouldNotInjectContextIntoMethodsThatDoesntHaveMiddlewareContextAnnotation() {
      // Arrange
      DummyClassB injectingObject = spy(new DummyClassB());

      // Act
      MiddlewareContextInjector.injectContext(contextContainer, injectingObject);

      // Assert
      verifyNonAnnotatedMethodsNotCalled(injectingObject, dummyContext);
    }
  }

  private static void verifyAnnotatedMethodsCalled(DummyClassB injectingObject, DummyType context) {
    verify(injectingObject, Mockito.times(1))
        .onPrivateAnnotatedMethodACalled(context);
    verify(injectingObject, Mockito.times(1))
        .protectedAnnotatedMethodA(context);
    verify(injectingObject, Mockito.times(1))
        .publicAnnotatedMethodA(context);

    verify(injectingObject, Mockito.times(1))
        .onPrivateAnnotatedMethodBCalled(context);
    verify(injectingObject, Mockito.times(1))
        .protectedAnnotatedMethodB(context);
    verify(injectingObject, Mockito.times(1))
        .publicAnnotatedMethodB(context);
  }

  private static void verifyNonAnnotatedMethodsNotCalled(DummyClassB injectingObject, DummyType context) {
    verify(injectingObject, Mockito.times(0))
        .onPrivateMethodACalled(any());
    verify(injectingObject, Mockito.times(0))
        .protectedMethodA(any());
    verify(injectingObject, Mockito.times(0))
        .publicMethodA(any());
    verify(injectingObject, Mockito.times(0))
        .publicMethodAWithAnotherAnnotation(context);

    verify(injectingObject, Mockito.times(0))
        .onPrivateMethodBCalled(any());
    verify(injectingObject, Mockito.times(0))
        .protectedMethodB(any());
    verify(injectingObject, Mockito.times(0))
        .publicMethodB(any());
    verify(injectingObject, Mockito.times(0))
        .publicMethodBWithAnotherAnnotation(context);
  }

  private static <R> List<R> duplicateList(R value, int size) {
    List<R> resultList = new ArrayList<>();
    for (int i = 0; i < size; ++i) {
      resultList.add(value);
    }
    return resultList;
  }

  private static List<Object> getValuesOfFields(Object obj, List<String> fieldNames) {
    List<Object> values = new ArrayList<>();
    for (String fieldName : fieldNames) {
      Object value;
      try {
        value = getDeclaredFieldValue(obj, fieldName);
      } catch (NoSuchFieldException e) {
        // Intentionally bypass
        value = null;
      }
      values.add(value);
    }
    return values;
  }

  // region Dummy classes for testing
  @Target({ ElementType.METHOD, ElementType.FIELD })
  @Retention(RetentionPolicy.RUNTIME)
  @interface DummyAnnotation {}

  class DummyClassA {
    @MiddlewareContext
    private DummyType privateAnnotatedFieldA;
    private DummyType privateFieldA;
    @MiddlewareContext
    protected DummyType protectedAnnotatedFieldA;
    protected DummyType protectedFieldA;
    @MiddlewareContext
    public DummyType publicAnnotatedFieldA;
    public DummyType publicFieldA;
    @DummyAnnotation
    public DummyType publicFieldAWithAnotherAnnotation;

    public DummyClassA() {}

    public DummyClassA(DummyType annotatedFieldValue, DummyType nonAnnotatedFieldValue) {
      privateAnnotatedFieldA = protectedAnnotatedFieldA = publicAnnotatedFieldA = annotatedFieldValue;
      privateFieldA = protectedFieldA = publicFieldA = publicFieldAWithAnotherAnnotation = nonAnnotatedFieldValue;
    }

    @MiddlewareContext
    private void privateAnnotatedMethodA(DummyType context) {
      onPrivateAnnotatedMethodACalled(context);
    }

    private void privateMethodA(DummyType context) {
      onPrivateMethodACalled(context);
    }

    @MiddlewareContext
    protected void protectedAnnotatedMethodA(DummyType context) {}

    protected void protectedMethodA(DummyType context) {}

    @MiddlewareContext
    public void publicAnnotatedMethodA(DummyType context) {}

    public void publicMethodA(DummyType context) {}

    @DummyAnnotation
    public void publicMethodAWithAnotherAnnotation(DummyType context) {}

    protected void onPrivateAnnotatedMethodACalled(DummyType context) {}

    protected void onPrivateMethodACalled(DummyType context) {}
  }

  class DummyClassB extends DummyClassA {
    @MiddlewareContext
    private DummyType privateAnnotatedFieldB;
    private DummyType privateFieldB;
    @MiddlewareContext
    protected DummyType protectedAnnotatedFieldB;
    protected DummyType protectedFieldB;
    @MiddlewareContext
    public DummyType publicAnnotatedFieldB;
    public DummyType publicFieldB;
    @DummyAnnotation
    public DummyType publicFieldBWithAnotherAnnotation;

    public DummyClassB() {}

    public DummyClassB(DummyType annotatedFieldValue, DummyType nonAnnotatedFieldValue) {
      super(annotatedFieldValue, nonAnnotatedFieldValue);
      privateAnnotatedFieldB = protectedAnnotatedFieldB = publicAnnotatedFieldB = annotatedFieldValue;
      privateFieldB = protectedFieldB = publicFieldB = publicFieldBWithAnotherAnnotation = nonAnnotatedFieldValue;
    }

    @MiddlewareContext
    private void privateAnnotatedMethodB(DummyType context) {
      onPrivateAnnotatedMethodBCalled(context); // For verify calls when testing
    }

    private void privateMethodB(DummyType context) {
      onPrivateMethodBCalled(context); // For verify calls when testing
    }

    @MiddlewareContext
    protected void protectedAnnotatedMethodB(DummyType context) {}

    protected void protectedMethodB(DummyType context) {}

    @MiddlewareContext
    public void publicAnnotatedMethodB(DummyType context) {}

    public void publicMethodB(DummyType context) {}

    @DummyAnnotation
    public void publicMethodBWithAnotherAnnotation(DummyType context) {}

    protected void onPrivateAnnotatedMethodBCalled(DummyType context) {}

    protected void onPrivateMethodBCalled(DummyType context) {}
  }

  class DummyType {}
  // endregion
}
