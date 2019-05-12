package net.dathoang.cqrs.commandbus.exceptions;

public class CommandBusException extends RuntimeException {

  public CommandBusException(String message) {
    super(message);
  }
}
