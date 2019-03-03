package net.dathoang.cqrs.commandbus.command;

import java.util.List;
import net.dathoang.cqrs.commandbus.middleware.Middleware;

public class CommandBusFactory {
  public static CommandBus create(CommandHandlerFactory commandHandlerFactory, List<Middleware> middlewareList) {
    return new DefaultCommandBus(commandHandlerFactory, middlewareList);
  }
}
