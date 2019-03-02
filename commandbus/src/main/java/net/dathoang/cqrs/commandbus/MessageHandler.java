package net.dathoang.cqrs.commandbus;

interface MessageHandler<C extends Message<R>, R> {
  R handle(C command) throws Exception;
}
