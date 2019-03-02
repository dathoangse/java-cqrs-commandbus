package net.dathoang.cqrs.commandbus;

public interface Middleware {
  <R> void preHandle(Message<R> message, ResultAndExceptionHolder<R> resultAndExceptionHolder);
  <R> void postHandle(Message<R> message, ResultAndExceptionHolder<R> resultAndExceptionHolder);
}
