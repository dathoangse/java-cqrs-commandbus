package net.dathoang.cqrs.commandbus.message;

public interface MessageHandlerFactory {
  <R> MessageHandler<Message<R>, R> createHandler(String messageName);
}
