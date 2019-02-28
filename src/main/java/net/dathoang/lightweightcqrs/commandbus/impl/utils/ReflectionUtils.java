package net.dathoang.lightweightcqrs.commandbus.impl.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReflectionUtils {
  private static final Log log = LogFactory.getLog(ReflectionUtils.class);

  private ReflectionUtils() {}

  public static List<Method> getAllDeclaredMethodsAnnotatedWith(Class cls, Class<? extends Annotation> annotationClass) {
    // Retrieve all reflecting classes
    List<Class> reflectingClasses = new ArrayList<>();
    reflectingClasses.add(cls);
    reflectingClasses.addAll(getAllSuperClasses(cls));

    // Retrieve all methods annotated with annotationClass
    List<Method> resultMethods = new ArrayList<>();
    for (Class reflectingClass : reflectingClasses) {
      resultMethods.addAll(
          getDeclaredMethodsAnnotatedWithForSingleClass(reflectingClass, annotationClass)
      );
    }

    return resultMethods;
  }

  public static List<Field> getAllDeclaredFieldsAnnotatedWith(Class cls, Class<? extends Annotation> annotationClass) {
    // Retrieve reflecting Classes
    List<Class> reflectingClasses = new ArrayList<>();
    reflectingClasses.add(cls);
    reflectingClasses.addAll(getAllSuperClasses(cls));

    // Retrieve all fields annotated with annotationClass
    List<Field> resultFields = new ArrayList<>();
    for (Class reflectingClass : reflectingClasses) {
      resultFields.addAll(
          getDeclaredFieldsAnnotatedWithForSingleClass(reflectingClass, annotationClass)
      );
    }

    return resultFields;
  }

  public static Object getDeclaredFieldValue(Object obj, String fieldName)
      throws NoSuchFieldException {
    List<Class> reflectingClasses = new ArrayList<>();
    reflectingClasses.add(obj.getClass());
    reflectingClasses.addAll(getAllSuperClasses(obj.getClass()));

    for (Class reflectingClass : reflectingClasses) {
      try {
        Field field = reflectingClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
      } catch (NoSuchFieldException ex) {
        // Intentionally bypass
      } catch (IllegalAccessException ex) {
        log.error(String.format("Error while getting value of the field %s on %s",
            fieldName, obj.getClass().getName()));
      }
    }

    throw new NoSuchFieldException(String.format("%s doesn't contain field %s",
        obj.getClass().getName(), fieldName));
  }

  private static List<Class> getAllSuperClasses(Class cls) {
    List<Class> superClasses = new ArrayList<>();

    Class currentSuperClass = cls.getSuperclass();
    while (currentSuperClass != null) {
      superClasses.add(currentSuperClass);
      currentSuperClass = currentSuperClass.getSuperclass();
    }

    return superClasses;
  }

  private static List<Method> getDeclaredMethodsAnnotatedWithForSingleClass(Class cls, Class<? extends Annotation> annotationClass) {
    List<Method> annotatedMethods = new ArrayList<>();
    for (Method method : cls.getDeclaredMethods()) {
      if (method.getAnnotation(annotationClass) != null) {
        annotatedMethods.add(method);
      }
    }
    return annotatedMethods;
  }

  private static List<Field> getDeclaredFieldsAnnotatedWithForSingleClass(Class cls, Class<? extends Annotation> annotationClass) {
    List<Field> annotatedFields = new ArrayList<>();
    for (Field field : cls.getDeclaredFields()) {
      if (field.getAnnotation(annotationClass) != null) {
        annotatedFields.add(field);
      }
    }
    return annotatedFields;
  }
}
