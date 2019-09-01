package net.dathoang.cqrs.commandbus.middleware;

import net.dathoang.cqrs.commandbus.message.Message;

import java.util.function.Function;

public interface Middleware {
  <R> R handle(Message<R> message, NextFunction<Message<R>, R> next) throws Exception;
}
