package net.dathoang.cqrs.commandbus.spring;

import net.dathoang.cqrs.commandbus.command.CommandHandlerFactory;
import net.dathoang.cqrs.commandbus.query.QueryHandlerFactory;
import org.springframework.context.ApplicationContext;

public class DefaultHandlerFactoryConfig implements HandlerFactoryConfig {
  private SpringAutoScanHandlerFactory springAutoScanHandlerFactory;

  public DefaultHandlerFactoryConfig(ApplicationContext context) {
    this.springAutoScanHandlerFactory = new SpringAutoScanHandlerFactory(context);
  }

  @Override
  public CommandHandlerFactory getCommandHandlerFactory() {
    return springAutoScanHandlerFactory;
  }

  @Override
  public QueryHandlerFactory getQueryhandlerFactory() {
    return springAutoScanHandlerFactory;
  }
}
