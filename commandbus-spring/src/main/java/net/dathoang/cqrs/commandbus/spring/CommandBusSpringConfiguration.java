package net.dathoang.cqrs.commandbus.spring;

import net.dathoang.cqrs.commandbus.command.CommandBus;
import net.dathoang.cqrs.commandbus.command.DefaultCommandBus;
import net.dathoang.cqrs.commandbus.query.DefaultQueryBus;
import net.dathoang.cqrs.commandbus.query.QueryBus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CommandBusSpringConfiguration {
  private final Log log = LogFactory.getLog(CommandBusSpringConfiguration.class);

  private ApplicationContext applicationContext;

  @Autowired
  public CommandBusSpringConfiguration(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Bean
  public CommandBus getCommandBus() {
    return new DefaultCommandBus(
        findHandlerFactoryConfig().getCommandHandlerFactory(),
        findMiddlewareConfig().getCommandMiddlewarePipeline()
    );
  }

  @Bean
  public QueryBus getQueryBus() {
    return new DefaultQueryBus(
        findHandlerFactoryConfig().getQueryhandlerFactory(),
        findMiddlewareConfig().getQueryMiddlewarePipeline()
    );
  }

  private HandlerFactoryConfig findHandlerFactoryConfig() {
    HandlerFactoryConfig scannedHandlerFactoryConfig = scanHandlerFactoryConfig();
    if (scannedHandlerFactoryConfig != null) {
      return scannedHandlerFactoryConfig;
    }

    log.info("Custom @Configuration-annotated HandlerFactoryConfig not found, using DefaultHandlerFactoryConfig");
    return new DefaultHandlerFactoryConfig(applicationContext);
  }

  private MiddlewareConfig findMiddlewareConfig() {
    MiddlewareConfig scannedMiddlewareConfig = scanMiddlewareConfig();
    if (scannedMiddlewareConfig != null) {
      return scannedMiddlewareConfig;
    }

    log.info("Custom @Configuration-annotated MiddlewareConfig not found, using DefaultMiddlewareConfig");
    return new DefaultMiddlewareConfig();
  }

  private HandlerFactoryConfig scanHandlerFactoryConfig() {
    return (HandlerFactoryConfig) applicationContext.getBeansWithAnnotation(Configuration.class).values().stream()
        .filter(c -> c instanceof HandlerFactoryConfig)
        .findFirst()
        .orElse(null);
  }

  private MiddlewareConfig scanMiddlewareConfig() {
    return (MiddlewareConfig) applicationContext.getBeansWithAnnotation(Configuration.class).values().stream()
        .filter(c -> c instanceof MiddlewareConfig)
        .findFirst()
        .orElse(null);
  }
}
