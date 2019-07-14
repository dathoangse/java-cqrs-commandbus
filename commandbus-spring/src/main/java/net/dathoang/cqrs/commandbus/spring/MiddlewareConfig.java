package net.dathoang.cqrs.commandbus.spring;

import net.dathoang.cqrs.commandbus.middleware.Middleware;

import java.util.List;

public interface MiddlewareConfig {
  List<Middleware> getCommandMiddlewarePipeline();
  List<Middleware> getQueryMiddlewarePipeline();
}
