package net.dathoang.lightweightcqrs.commandbus.impl.bus;

import static java.util.Arrays.asList;
import static net.dathoang.lightweightcqrs.commandbus.impl.utils.ReflectionUtils.getDeclaredFieldValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
import java.util.Collections;
import java.util.List;
import net.dathoang.lightweightcqrs.commandbus.annotations.MiddlewareContext;
import net.dathoang.lightweightcqrs.commandbus.interfaces.PipelineContextContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MiddlewareContextInjectorTest {
  private DummyType dummyContext;
  private PipelineContextContainer contextContainer;
  private static final List<String> annotatedFieldNames = asList("dummyFieldA1", "dummyFieldA3", "dummyFieldA5", "dummyFieldB1", "dummyFieldB3", "dummyFieldB5");
  private static final List<String> nonContextFieldNames = asList("dummyFieldA2", "dummyFieldA4", "dummyFieldA6", "dummyFieldA7", "dummyFieldB2", "dummyFieldB4", "dummyFieldB6", "dummyFieldB7");

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
        .onDummyMethodA1Called(context);
    verify(injectingObject, Mockito.times(1))
        .dummyMethodA3(context);
    verify(injectingObject, Mockito.times(1))
        .dummyMethodA5(context);

    verify(injectingObject, Mockito.times(1))
        .onDummyMethodB1Called(context);
    verify(injectingObject, Mockito.times(1))
        .dummyMethodB3(context);
    verify(injectingObject, Mockito.times(1))
        .dummyMethodB5(context);
  }

  private static void verifyNonAnnotatedMethodsNotCalled(DummyClassB injectingObject, DummyType context) {
    verify(injectingObject, Mockito.times(0))
        .onDummyMethodA2Called(any());
    verify(injectingObject, Mockito.times(0))
        .dummyMethodA4(any());
    verify(injectingObject, Mockito.times(0))
        .dummyMethodA6(any());
    verify(injectingObject, Mockito.times(0))
        .dummyMethodA7(context);

    verify(injectingObject, Mockito.times(0))
        .onDummyMethodB2Called(any());
    verify(injectingObject, Mockito.times(0))
        .dummyMethodB4(any());
    verify(injectingObject, Mockito.times(0))
        .dummyMethodB6(any());
    verify(injectingObject, Mockito.times(0))
        .dummyMethodB7(context);
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
}


@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@interface DummyAnnotation {}

class DummyClassA {
  @MiddlewareContext
  private DummyType dummyFieldA1;
  private DummyType dummyFieldA2;

  @MiddlewareContext
  protected DummyType dummyFieldA3;
  protected DummyType dummyFieldA4;

  @MiddlewareContext
  public DummyType dummyFieldA5;
  public DummyType dummyFieldA6;

  @DummyAnnotation
  public DummyType dummyFieldA7;

  public DummyClassA() {}

  public DummyClassA(DummyType annotatedFieldValue, DummyType nonAnnotatedFieldValue) {
    dummyFieldA1 = dummyFieldA3 = dummyFieldA5 = annotatedFieldValue;
    dummyFieldA2 = dummyFieldA4 = dummyFieldA6 = dummyFieldA7 = nonAnnotatedFieldValue;
  }

  @MiddlewareContext
  private void dummyMethodA1(DummyType context) {
    onDummyMethodA1Called(context);
  }
  private void dummyMethodA2(DummyType context) {
    onDummyMethodA2Called(context);
  }

  @MiddlewareContext
  protected void dummyMethodA3(DummyType context) {}
  protected void dummyMethodA4(DummyType context) {}

  @MiddlewareContext
  public void dummyMethodA5(DummyType context) {}
  public void dummyMethodA6(DummyType context) {}

  @DummyAnnotation
  public void dummyMethodA7(DummyType context) {}

  protected void onDummyMethodA1Called(DummyType context) {}

  protected void onDummyMethodA2Called(DummyType context) {}
}

class DummyClassB extends DummyClassA {
  @MiddlewareContext
  private DummyType dummyFieldB1;
  private DummyType dummyFieldB2;

  @MiddlewareContext
  protected DummyType dummyFieldB3;
  protected DummyType dummyFieldB4;

  @MiddlewareContext
  public DummyType dummyFieldB5;
  public DummyType dummyFieldB6;

  @DummyAnnotation
  public DummyType dummyFieldB7;

  public DummyClassB() {}

  public DummyClassB(DummyType annotatedFieldValue, DummyType nonAnnotatedFieldValue) {
    super(annotatedFieldValue, nonAnnotatedFieldValue);
    dummyFieldB1 = dummyFieldB3 = dummyFieldB5 = annotatedFieldValue;
    dummyFieldB2 = dummyFieldB4 = dummyFieldB6 = dummyFieldB7 = nonAnnotatedFieldValue;
  }

  @MiddlewareContext
  private void dummyMethodB1(DummyType context) {
    onDummyMethodB1Called(context); // For verify calls when testing
  }
  private void dummyMethodB2(DummyType context) {
    onDummyMethodB2Called(context); // For verify calls when testing
  }

  @MiddlewareContext
  protected void dummyMethodB3(DummyType context) {}
  protected void dummyMethodB4(DummyType context) {}

  @MiddlewareContext
  public void dummyMethodB5(DummyType context) {}
  public void dummyMethodB6(DummyType context) {}

  @DummyAnnotation
  public void dummyMethodB7(DummyType context) {}

  protected void onDummyMethodB1Called(DummyType context) {}

  protected void onDummyMethodB2Called(DummyType context) {}
}

class DummyType {}
