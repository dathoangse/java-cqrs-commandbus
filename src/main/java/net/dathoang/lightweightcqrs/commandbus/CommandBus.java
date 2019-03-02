package net.dathoang.lightweightcqrs.commandbus;

public interface CommandBus {
  <R> R dispatch(Command<R> command) throws Exception;
}
