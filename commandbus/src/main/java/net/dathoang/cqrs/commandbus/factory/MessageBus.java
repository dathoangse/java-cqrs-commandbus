package net.dathoang.cqrs.commandbus.factory;

import net.dathoang.cqrs.commandbus.message.Message;

interface MessageBus {
  <R> R dispatch(Message<R> message) throws Exception;
}
