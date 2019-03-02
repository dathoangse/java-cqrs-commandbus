package net.dathoang.cqrs.commandbus;

public interface CommandBus {
  <R> R dispatch(Command<R> command) throws Exception;
}
