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

import static java.util.Arrays.asList;

public class AutoScanHandlerFactory implements QueryHandlerFactory, CommandHandlerFactory {
  private static final Log log = LogFactory.getLog(AutoScanHandlerFactory.class);

  private Map<String, Class<? extends QueryHandler>> handlerClassByQueryNameMap = new HashMap<>();
  private Map<String, Class<? extends CommandHandler>> handlerClassByCommandNameMap = new HashMap<>();

  private BeanFactory beanFactory;

  protected AutoScanHandlerFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  public void scanAndRegisterHandlers(String packageToScan) {
    log.info("Scanning query & command handlers in the package: " + packageToScan);
    Reflections reflections = new Reflections(packageToScan);
    Set<Class<? extends QueryHandler>> queryClasses = reflections.getSubTypesOf(QueryHandler.class);
    Set<Class<? extends CommandHandler>> commandClasses = reflections.getSubTypesOf(CommandHandler.class);

    queryClasses.forEach(queryClass -> {
      QueryMappings multiMappingAnnotation = queryClass.getAnnotation(QueryMappings.class);
      QueryMapping mappingAnnotation = queryClass.getAnnotation(QueryMapping.class);
      if (multiMappingAnnotation != null) {
        List<QueryMapping> queryMappings = asList(multiMappingAnnotation.value());
        queryMappings.forEach(queryMapping -> {
          log.info(String.format("Registering handler %s to handle the query %s",
              queryClass.getSimpleName(), queryMapping.value().getName()));

          handlerClassByQueryNameMap.put(queryMapping.value().getName(), queryClass);
        });
      } else if (mappingAnnotation != null) {
        log.info(String.format("Registering handler %s to handle the query %s",
            queryClass.getSimpleName(), mappingAnnotation.value().getName()));

        handlerClassByQueryNameMap.put(mappingAnnotation.value().getName(), queryClass);
      }
    });

    commandClasses.forEach(commandClass -> {
      CommandMappings multiMappingAnnotation = commandClass.getAnnotation(CommandMappings.class);
      CommandMapping mappingAnnotation = commandClass.getAnnotation(CommandMapping.class);
      if (multiMappingAnnotation != null) {
        List<CommandMapping> commandMappings = asList(multiMappingAnnotation.value());
        commandMappings.forEach(commandMapping -> {
          log.info(String.format("Registering handler %s to handle the command %s",
              commandClass.getSimpleName(), commandMapping.value().getName()));

          handlerClassByCommandNameMap.put(commandMapping.value().getName(), commandClass);
        });
      } else if (mappingAnnotation != null) {
        log.info(String.format("Registering handler %s to handle the command %s",
            commandClass.getSimpleName(), mappingAnnotation.value().getName()));

        handlerClassByCommandNameMap.put(mappingAnnotation.value().getName(), commandClass);
      }
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R> QueryHandler<Query<R>, R> createQueryHandler(String queryName) {
    Class<? extends QueryHandler> handlerClass = handlerClassByQueryNameMap.get(queryName);
    if (handlerClass == null) {
      return null;
    }

    return beanFactory.createBean(handlerClass);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R> CommandHandler<Command<R>, R> createCommandHandler(String commandName) {
    Class<? extends CommandHandler> handlerClass = handlerClassByCommandNameMap.get(commandName);
    if (handlerClass == null) {
      return null;
    }

    return beanFactory.createBean(handlerClass);
  }
}
