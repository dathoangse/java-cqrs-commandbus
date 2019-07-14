package net.dathoang.cqrs.commandbus.autoscan;

import net.dathoang.cqrs.commandbus.command.Command;
import net.dathoang.cqrs.commandbus.command.CommandHandler;
import net.dathoang.cqrs.commandbus.command.CommandHandlerFactory;
import net.dathoang.cqrs.commandbus.query.Query;
import net.dathoang.cqrs.commandbus.query.QueryHandler;
import net.dathoang.cqrs.commandbus.query.QueryHandlerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

import java.util.*;
import java.util.function.Function;

public abstract class AutoScanHandlerFactory implements QueryHandlerFactory, CommandHandlerFactory {
  private static final Log log = LogFactory.getLog(AutoScanHandlerFactory.class);

  private Map<String, Class<? extends QueryHandler>> handlerClassByQueryNameMap = new HashMap<>();
  private Map<String, Class<? extends CommandHandler>> handlerClassByCommandNameMap = new HashMap<>();

  public AutoScanHandlerFactory() {}

  public void startScanningHandler() {
    try {
      scanHandlers();
    } catch (Exception ex) {
      log.error("Error while scanning packages for query handlers", ex);
    }
  }

  private void scanAndRegisterHandler(String packageToScan) {
    log.info("Scanning query & command handlers in the package: " + packageToScan);
    Reflections reflections = new Reflections(packageToScan);
    Set<Class<? extends QueryHandler>> queryClasses = reflections.getSubTypesOf(QueryHandler.class);
    Set<Class<? extends CommandHandler>> commandClasses = reflections.getSubTypesOf(CommandHandler.class);

    queryClasses.forEach(queryClass -> {
      QueryMapping mappingAnnotation = queryClass.getAnnotation(QueryMapping.class);
      if (mappingAnnotation != null) {
        log.info(String.format("Registering handler %s to handle the query %s",
            queryClass.getSimpleName(), mappingAnnotation.value().getName()));

        handlerClassByQueryNameMap.put(mappingAnnotation.value().getName(), queryClass);
      }
    });

    commandClasses.forEach(commandClass -> {
      CommandMapping mappingAnnotation = commandClass.getAnnotation(CommandMapping.class);
      if (mappingAnnotation != null) {
        log.info(String.format("Registering handler %s to handle the command %s",
            commandClass.getSimpleName(), mappingAnnotation.value().getName()));

        handlerClassByCommandNameMap.put(mappingAnnotation.value().getName(), commandClass);
      }
    });
  }

  @Override
  public <R> QueryHandler<Query<R>, R> createQueryHandler(String queryName) {
    Class<? extends QueryHandler> handlerClass = handlerClassByQueryNameMap.get(queryName);
    if (handlerClass == null) {
      return null;
    }

    return QueryHandler.class.cast(getBeanFactory().apply(handlerClass));
  }

  @Override
  public <R> CommandHandler<Command<R>, R> createCommandHandler(String commandName) {
    Class<? extends CommandHandler> handlerClass = handlerClassByCommandNameMap.get(commandName);
    if (handlerClass == null) {
      return null;
    }

    return CommandHandler.class.cast(getBeanFactory().apply(handlerClass));
  }

  protected abstract Function<Class, Object> getBeanFactory();

  protected abstract Set<String> getPackagesToScanConfig();

  private void scanHandlers() {
    // Get packages to scan via annotation
    Set<String> packagesToScan = getPackagesToScanConfig();

    if (packagesToScan.isEmpty()) {
      log.warn("No packages to scan for query handlers. Please put the packages to scan in @ComponentScan annotation of SpringBootApplication class.");
    }
    packagesToScan.forEach(packageToScan -> {
      log.info("Scanning query handler in the package " + packageToScan);
      scanAndRegisterHandler(packageToScan);
    });
  }
}
