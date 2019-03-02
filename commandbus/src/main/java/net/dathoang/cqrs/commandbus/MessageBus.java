package net.dathoang.cqrs.commandbus;

interface MessageBus {
  <R> R dispatch(Message<R> message) throws Exception;
}
