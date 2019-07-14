package net.dathoang.cqrs.commandbus.spring;

import net.dathoang.cqrs.commandbus.command.CommandHandlerFactory;
import net.dathoang.cqrs.commandbus.query.QueryHandlerFactory;

public interface HandlerFactoryConfig {
  CommandHandlerFactory getCommandHandlerFactory();
  QueryHandlerFactory getQueryhandlerFactory();
}
