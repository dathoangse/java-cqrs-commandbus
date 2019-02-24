package net.dathoang.lightweightcqrs.commandbus.interfaces;

import net.dathoang.lightweightcqrs.commandbus.models.ResultAndExceptionHolder;

public interface Middleware {
  <R> void preHandle(Command<R> command, ResultAndExceptionHolder<R> resultAndExceptionHolder);
  <R> void postHandle(Command<R> command, ResultAndExceptionHolder<R> resultAndExceptionHolder);
}
