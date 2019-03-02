package net.dathoang.cqrs.commandbus;

public interface MessageBus {
  <R> R dispatch(Message<R> message) throws Exception;
}
