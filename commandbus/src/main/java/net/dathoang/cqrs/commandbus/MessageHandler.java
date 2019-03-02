package net.dathoang.cqrs.commandbus;

interface MessageHandler<T extends Message<R>, R> {
  R handle(T message) throws Exception;
}
