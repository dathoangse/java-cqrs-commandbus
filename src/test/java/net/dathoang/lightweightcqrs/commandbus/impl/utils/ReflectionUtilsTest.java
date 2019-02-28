package net.dathoang.lightweightcqrs.commandbus.impl.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import net.dathoang.lightweightcqrs.commandbus.annotations.MiddlewareContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReflectionUtilsTest {
  private DummyClassA dummyClassA;
  private DummyClassB dummyClassB;

  @BeforeEach
  void setUp() {
    // Arrange
    dummyClassA = mock(DummyClassA.class);
    dummyClassB = mock(DummyClassB.class);
  }

  @Nested
  @DisplayName("getAllDeclaredFieldsAnnotatedWith()")
  class GetAllDeclaredFieldsAnnotatedWithTest {
    @Test
    @DisplayName("should return the class's and superclass's all annotated fields (including public, protected, private)")
    void shouldReturnTheClassAndSuperClassAllAnnotatedFields() {
      // Act
      List<Field> fields = ReflectionUtils.getAllDeclaredFieldsAnnotatedWith(dummyClassB.getClass(), DummyAnnotation.class);

      // Assert
      assertThat(fields.stream().map(it -> it.getName()))
          .containsExactlyInAnyOrder(
              "dummyFieldA1",
              "dummyFieldA3",
              "dummyFieldA5",
              "dummyFieldB1",
              "dummyFieldB3",
              "dummyFieldB5"
          );
    }
  }

  @Nested
  @DisplayName("getAllDeclaredMethodsAnnotatedWith()")
  class GetAllDeclaredMethodsAnnotatedWithTest {
    @Test
    @DisplayName("should return the class's and superclass's all annotated fields (including public, private and protected)")
    void shouldReturnTheClassAndSuperClassAllAnnotatedMethods() {
      // Act
      List<Method> methods = ReflectionUtils.getAllDeclaredMethodsAnnotatedWith(dummyClassB.getClass(), DummyAnnotation.class);

      // Assert
      assertThat(methods.stream().map(it -> it.getName()))
          .containsExactlyInAnyOrder(
              "dummyMethodA1",
              "dummyMethodA3",
              "dummyMethodA5",
              "dummyMethodB1",
              "dummyMethodB3",
              "dummyMethodB5"
          );
    }
  }

  // TODO: Test getDeclaredFieldValue
}

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@interface DummyAnnotation {}

@interface AnotherDummyAnnotation {}

class DummyClassA {
  @DummyAnnotation
  private Object dummyFieldA1;
  private Object dummyFieldA2;

  @DummyAnnotation
  protected Object dummyFieldA3;
  protected Object dummyFieldA4;

  @DummyAnnotation
  public Object dummyFieldA5;
  public Object dummyFieldA6;

  @AnotherDummyAnnotation
  public Object dummyFieldA7;

  @DummyAnnotation
  private void dummyMethodA1() {}
  private void dummyMethodA2() {}

  @DummyAnnotation
  protected void dummyMethodA3() {}
  protected void dummyMethodA4() {}

  @DummyAnnotation
  public void dummyMethodA5() {}
  public void dummyMethodA6() {}

  @AnotherDummyAnnotation
  public void dummyMethodA7() {}
}

class DummyClassB extends DummyClassA {
  @DummyAnnotation
  private Object dummyFieldB1;
  private Object dummyFieldB2;

  @DummyAnnotation
  protected Object dummyFieldB3;
  protected Object dummyFieldB4;

  @DummyAnnotation
  public Object dummyFieldB5;
  public Object dummyFieldB6;

  @AnotherDummyAnnotation
  public Object dummyFieldB7;

  @DummyAnnotation
  private void dummyMethodB1() {}
  private void dummyMethodB2() {}

  @DummyAnnotation
  protected void dummyMethodB3() {}
  protected void dummyMethodB4() {}

  @DummyAnnotation
  public void dummyMethodB5() {}
  public void dummyMethodB6() {}

  @AnotherDummyAnnotation
  public void dummyMethodB7() {}
}

