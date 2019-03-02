package net.dathoang.cqrs.commandbus;

interface MessageHandlerFactory {
  <R> MessageHandler<Message<R>, R> createHandler(String commandName);
}
