package net.dathoang.lightweightcqrs.commandbus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class MiddlewareContextInjector {
  private static final Log log = LogFactory.getLog(MiddlewareContextInjector.class);

  private MiddlewareContextInjector() {}

  static void injectContext(PipelineContextContainer contextContainer, Object injectingObject) {
    injectContextIntoMethods(contextContainer, injectingObject);
    injectContextIntoFields(contextContainer, injectingObject);
  }

  private static void injectContextIntoMethods(PipelineContextContainer contextContainer, Object injectingObject) {
    List<Method> injectingMethods = ReflectionUtils.getAllDeclaredMethodsAnnotatedWith(
        injectingObject.getClass(), MiddlewareContext.class);

    for (Method injectingMethod : injectingMethods) {
      injectingMethod.setAccessible(true);
      Class[] parameterTypes = injectingMethod.getParameterTypes();

      // Build argument list
      List<Object> arguments = new ArrayList<>();
      for (Class parameterType : parameterTypes) {
        if (parameterType.equals(PipelineContextContainer.class)) {
          arguments.add(contextContainer);
        } else {
          Object context = contextContainer.resolveContext(parameterType);
          if (context == null) {
            log.error(String.format("Can't find context of type %s in context container to inject into %s",
                    parameterType.getName(), injectingObject.getClass().getName()));
          }
          arguments.add(context);
        }
      }

      // Invoke method to inject contexts
      try {
        injectingMethod.invoke(injectingObject, arguments.toArray(new Object[arguments.size()]));
      } catch (Exception ex) {
        log.error(String.format("Error while trying to inject contexts into %s by invoking %s method",
            injectingObject.getClass().getName(), injectingMethod.getClass().getName()));
      }
    }
  }

  private static void injectContextIntoFields(PipelineContextContainer contextContainer, Object injectingObject) {
    List<Field> injectingFields = ReflectionUtils.getAllDeclaredFieldsAnnotatedWith(
        injectingObject.getClass(), MiddlewareContext.class);

    for (Field injectingField : injectingFields) {
      injectingField.setAccessible(true);

      try {
        if (injectingField.getType().equals(PipelineContextContainer.class)) {
          injectingField.set(injectingObject, contextContainer);
        } else {
          Object context = contextContainer.resolveContext(injectingField.getType());
          if (context != null) {
            injectingField.set(injectingObject, context);
          } else {
            log.error(String.format("Can't find context of type %s in context container to inject into %s",
                injectingField.getType().getName(), injectingObject.getClass().getName()));
          }
        }
      } catch (IllegalAccessException ex) {
        log.error(String.format("Error while trying to inject context into %s via field %s",
            injectingObject.getClass().getName(), injectingField.getName()));
      }
    }
  }
}
