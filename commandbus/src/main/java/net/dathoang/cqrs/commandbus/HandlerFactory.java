package net.dathoang.cqrs.commandbus;

public interface HandlerFactory {
  <R> Handler<Message<R>, R> createHandler(String commandName);
}
