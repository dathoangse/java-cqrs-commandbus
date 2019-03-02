package net.dathoang.cqrs.commandbus.factory;

import net.dathoang.cqrs.commandbus.message.Message;

interface MessageHandler<T extends Message<R>, R> {
  R handle(T message) throws Exception;
}
