package net.dathoang.lightweightcqrs.commandbus.exceptions;

import net.dathoang.lightweightcqrs.commandbus.Command;

public class NoCommandHandlerFoundException extends RuntimeException {
  public NoCommandHandlerFoundException(Class<? extends Command> commandClass) {
    super(String.format("No command handler found for %s", commandClass.getName()));
  }
}
