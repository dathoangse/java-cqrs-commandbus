package net.dathoang.cqrs.commandbus.exceptions;

import net.dathoang.cqrs.commandbus.message.Message;

public class NoHandlerFoundException extends RuntimeException {
  public NoHandlerFoundException(Class<? extends Message> messageClass) {
    super(String.format("No handler found for %s", messageClass.getName()));
  }
}
