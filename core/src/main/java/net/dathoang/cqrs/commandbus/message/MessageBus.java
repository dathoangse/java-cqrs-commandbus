package net.dathoang.cqrs.commandbus.message;

import net.dathoang.cqrs.commandbus.message.Message;

public interface MessageBus {
  <R> R dispatch(Message<R> message) throws Exception;
}
