package net.dathoang.cqrs.commandbus.message;

import java.util.HashMap;
import java.util.Map;
import net.dathoang.cqrs.commandbus.middleware.Middleware;
import net.dathoang.cqrs.commandbus.middleware.PipelineContextContainer;

final class DefaultPipelineContextContainer implements PipelineContextContainer {
  private Map<String, Object> middlewareDataMap = new HashMap<>();
  private Map<String, Object> contextMap = new HashMap<>();

  @Override
  public <R> R getMiddlewareData(Class<? extends Middleware> middlewareClass, String key) {
    String realKey = formatMiddlewareDataKey(middlewareClass, key);
    return (R)middlewareDataMap.get(realKey);
  }

  @Override
  public <R> void setMiddlewareData(Class<? extends Middleware> middlewareClass, String key,
      R value) {
    String realKey = formatMiddlewareDataKey(middlewareClass, key);
    middlewareDataMap.put(realKey, value);
  }

  @Override
  public <R> void bindContext(Class<R> contextClass, R instance) {
    contextMap.put(contextClass.getName(), instance);
  }

  @Override
  public <R> R resolveContext(Class<R> contextClass) {
    return contextClass.cast(contextMap.get(contextClass.getName()));
  }

  private String formatMiddlewareDataKey(Class<? extends Middleware> middlewareClass, String key) {
    return String.format("%s:%s", middlewareClass.getName(), key);
  }
}
