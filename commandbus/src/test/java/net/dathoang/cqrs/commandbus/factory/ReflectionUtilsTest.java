package net.dathoang.cqrs.commandbus.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import net.dathoang.cqrs.commandbus.factory.ReflectionUtils;
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
      List<Field> fields = ReflectionUtils
          .getAllDeclaredFieldsAnnotatedWith(dummyClassB.getClass(), DummyAnnotation.class);

      // Assert
      assertThat(fields.stream().map(it -> it.getName()))
          .containsExactlyInAnyOrder(
              "privateAnnotatedFieldA",
              "protectedAnnotatedFieldA",
              "publicAnnotatedFieldA",
              "privateAnnotatedFieldB",
              "protectedAnnotatedFieldB",
              "publicAnnotatedFieldB"
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
              "privateAnnotatedMethodA",
              "protectedAnnotatedMethodA",
              "publicAnnotatedMethodA",
              "privateAnnotatedMethodB",
              "protectedAnnotatedMethodB",
              "publicAnnotatedMethodB"
          );
    }
  }

  // region Dummy classes for testing
  @Target({ ElementType.METHOD, ElementType.FIELD })
  @Retention(RetentionPolicy.RUNTIME)
  @interface DummyAnnotation {}

  @interface AnotherDummyAnnotation {}

  class DummyClassA {
    @DummyAnnotation
    private Object privateAnnotatedFieldA;
    private Object privateFieldA;

    @DummyAnnotation
    protected Object protectedAnnotatedFieldA;
    protected Object protectedFieldA;

    @DummyAnnotation
    public Object publicAnnotatedFieldA;
    public Object publicFieldA;

    @AnotherDummyAnnotation
    public Object publicFieldAWithAnotherAnnotation;

    @DummyAnnotation
    private void privateAnnotatedMethodA() {}
    private void privateMethodA() {}

    @DummyAnnotation
    protected void protectedAnnotatedMethodA() {}
    protected void protectedMethodA() {}

    @DummyAnnotation
    public void publicAnnotatedMethodA() {}
    public void publicMethodA() {}

    @AnotherDummyAnnotation
    public void publicMethodAWithAnotherAnnotation() {}
  }

  class DummyClassB extends DummyClassA {
    @DummyAnnotation
    private Object privateAnnotatedFieldB;
    private Object privateFieldB;

    @DummyAnnotation
    protected Object protectedAnnotatedFieldB;
    protected Object protectedFieldB;

    @DummyAnnotation
    public Object publicAnnotatedFieldB;
    public Object publicFieldB;

    @AnotherDummyAnnotation
    public Object publicFieldBWithAnotherAnnotation;

    @DummyAnnotation
    private void privateAnnotatedMethodB() {}
    private void privateMethodB() {}

    @DummyAnnotation
    protected void protectedAnnotatedMethodB() {}
    protected void protectedMethodB() {}

    @DummyAnnotation
    public void publicAnnotatedMethodB() {}
    public void publicMethodB() {}

    @AnotherDummyAnnotation
    public void publicMethodBWithAnotherAnnotation() {}
    
    // endregion
  }
}



