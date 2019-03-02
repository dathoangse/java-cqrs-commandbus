package net.dathoang.cqrs.commandbus.exceptions;

public class InvalidMessageTypeException extends CommandBusException {

  public InvalidMessageTypeException(String message, Throwable cause) {
    super(message, cause);
  }
}
