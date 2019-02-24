package net.dathoang.lightweightcqrs.commandbus.interfaces;

public interface CommandBus {
  <R> R dispatch(Command<R> command) throws Exception;
}
