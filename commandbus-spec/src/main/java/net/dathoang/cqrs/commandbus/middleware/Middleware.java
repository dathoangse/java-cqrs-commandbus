package net.dathoang.cqrs.commandbus.middleware;

import net.dathoang.cqrs.commandbus.message.Message;

public interface Middleware {
  <R> R handle(Message<R> message, NextMiddlewareFunction<Message<R>, R> next) throws Exception;
}
