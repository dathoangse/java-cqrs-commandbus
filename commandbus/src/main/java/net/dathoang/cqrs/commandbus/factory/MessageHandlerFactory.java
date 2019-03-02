package net.dathoang.cqrs.commandbus.factory;

import net.dathoang.cqrs.commandbus.message.Message;

interface MessageHandlerFactory {
  <R> MessageHandler<Message<R>, R> createHandler(String messageName);
}
