package net.dathoang.cqrs.commandbus.command;

public interface CommandBus {
  <R> R dispatch(Command<R> command) throws Exception;
}
