package net.dathoang.cqrs.commandbus.message;

import net.dathoang.cqrs.commandbus.message.Message;

public interface MessageHandler<T extends Message<R>, R> {
  R handle(T message) throws Exception;
}
