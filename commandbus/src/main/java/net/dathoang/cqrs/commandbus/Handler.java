package net.dathoang.cqrs.commandbus;

public interface Handler<C extends Message<R>, R> {
  R handle(C command) throws Exception;
}
