package net.dathoang.cqrs.commandbus;

public interface Middleware {
  <R> void preHandle(Command<R> command, ResultAndExceptionHolder<R> resultAndExceptionHolder);
  <R> void postHandle(Command<R> command, ResultAndExceptionHolder<R> resultAndExceptionHolder);
}
