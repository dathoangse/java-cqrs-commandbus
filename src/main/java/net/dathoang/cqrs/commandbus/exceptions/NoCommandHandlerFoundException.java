package net.dathoang.cqrs.commandbus.exceptions;

import net.dathoang.cqrs.commandbus.Command;

public class NoCommandHandlerFoundException extends RuntimeException {
  public NoCommandHandlerFoundException(Class<? extends Command> commandClass) {
    super(String.format("No command handler found for %s", commandClass.getName()));
  }
}
