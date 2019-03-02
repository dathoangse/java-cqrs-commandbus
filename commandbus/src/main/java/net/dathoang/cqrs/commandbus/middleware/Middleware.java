package net.dathoang.cqrs.commandbus.middleware;

import net.dathoang.cqrs.commandbus.message.Message;

public interface Middleware {
  <R> void preHandle(Message<R> message, ResultAndExceptionHolder<R> resultAndExceptionHolder);
  <R> void postHandle(Message<R> message, ResultAndExceptionHolder<R> resultAndExceptionHolder);
}
