package net.dathoang.cqrs.commandbus.exceptions;

import net.dathoang.cqrs.commandbus.Message;

public class NoHandlerFoundException extends RuntimeException {
  public NoHandlerFoundException(Class<? extends Message> commandClass) {
    super(String.format("No handler found for %s", commandClass.getName()));
  }
}
