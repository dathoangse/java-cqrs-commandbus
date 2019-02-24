package net.dathoang.lightweightcqrs.commandbus.interfaces;

import net.dathoang.lightweightcqrs.commandbus.models.ExceptionHolder;
import net.dathoang.lightweightcqrs.commandbus.models.ResultHolder;

public interface Middleware {
  <R> void preHandle(Command<R> command, ResultHolder<R> resultHolder, ExceptionHolder exceptionHolder);
  <R> void postHandle(Command<R> command, ResultHolder<R> resultHolder, ExceptionHolder exceptionHolder);
}
