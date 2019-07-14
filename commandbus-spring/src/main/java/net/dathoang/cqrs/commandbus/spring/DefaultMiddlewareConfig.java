package net.dathoang.cqrs.commandbus.spring;

import net.dathoang.cqrs.commandbus.middleware.Middleware;
import net.dathoang.cqrs.commandbus.middleware.logging.LoggingMiddleware;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultMiddlewareConfig implements MiddlewareConfig {
  @Override
  public List<Middleware> getCommandMiddlewarePipeline() {
    return Collections.singletonList(
        new LoggingMiddleware()
    );
  }

  @Override
  public List<Middleware> getQueryMiddlewarePipeline() {
    return Collections.singletonList(
        new LoggingMiddleware()
    );
  }
}
