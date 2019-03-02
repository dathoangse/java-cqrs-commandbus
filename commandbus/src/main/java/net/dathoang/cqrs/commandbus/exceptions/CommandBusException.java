package net.dathoang.cqrs.commandbus.exceptions;

public class CommandBusException extends RuntimeException {

  public CommandBusException() {
  }

  public CommandBusException(String message) {
    super(message);
  }

  public CommandBusException(String message, Throwable cause) {
    super(message, cause);
  }

  public CommandBusException(Throwable cause) {
    super(cause);
  }

  public CommandBusException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
