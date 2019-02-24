package net.dathoang.lightweightcqrs.commandbus.bus;

import net.dathoang.lightweightcqrs.commandbus.exceptions.NoCommandHandlerFoundException;
import net.dathoang.lightweightcqrs.commandbus.interfaces.*;
import net.dathoang.lightweightcqrs.commandbus.models.ExceptionHolder;
import net.dathoang.lightweightcqrs.commandbus.models.ResultHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultCommandBus implements CommandBus {
  private static final Log log = LogFactory.getLog(DefaultCommandBus.class);

  private final List<Middleware> middlewarePipeline = new CopyOnWriteArrayList<>();
  private CommandHandlerFactory handlerFactory;

  /**
   * Find a {@link CommandHandler} that can handle this {@link Command} and dispatch it to the handler
   * @param command the command to dispatch
   * @param <R> the type of the result produced after handling the command
   * @return the result after handling the command
   * @throws NoCommandHandlerFoundException when the {@link CommandBus} found
   * @throws Exception
   */
  @Override
  public <R> R dispatch(Command<R> command) throws Exception {
    CommandHandler<Command<R>, R> handler = handlerFactory.createHandler(command.getClass().getName());
    if (handler == null) {
      throw new NoCommandHandlerFoundException(command.getClass());
    }
    return dispatchThroughMiddlewarePipeline(command, handler);
  }

  public List<Middleware> getMiddlewarePipeline() {
    return middlewarePipeline;
  }

  public CommandHandlerFactory getHandlerFactory() {
    return handlerFactory;
  }

  public void setHandlerFactory(CommandHandlerFactory handlerFactory) {
    this.handlerFactory = handlerFactory;
  }

  private <R> R dispatchThroughMiddlewarePipeline(Command<R> command, CommandHandler<Command<R>, R> handler) throws Exception {
    List<Middleware> processedMiddlewares = new ArrayList<>();
    ExceptionHolder exceptionHolder = new ExceptionHolder();
    ResultHolder<R> resultHolder = new ResultHolder<>();

    // Pre-handle the command
    for (int i = 0; i < middlewarePipeline.size(); ++i) {
      Middleware middleware = middlewarePipeline.get(i);
      try {
        processedMiddlewares.add(middleware);
        middleware.preHandle(command, resultHolder, exceptionHolder);
        if (exceptionHolder.getException() != null || resultHolder.getResult() != null) {
          break;
        }
      } catch (Exception ex) {
        log.error(
            String.format("Exception while %s is pre-handling %s, the error is intentionally bypassed",
                middleware.getClass().getName(), command.getClass().getName()),
            ex);
      }
    }

    // Handle the command
    if (resultHolder.getResult() == null && exceptionHolder.getException() == null) {
      try {
        R result = handler.handle(command);
        resultHolder.setResult(result);
      } catch (Exception ex) {
        log.error(
            String.format("Exception while %s is handling %s, the exception will be passed through the bottom to top " +
                    "of the middleware pipeline", handler.getClass().getName(), command.getClass().getName()),
            ex);
        exceptionHolder.setException(ex);
      }
    }

    // Post-handle the command
    for (int i = processedMiddlewares.size() - 1; i >= 0; --i) {
      Middleware middleware = middlewarePipeline.get(i);
      try {
        middleware.postHandle(command, resultHolder, exceptionHolder);
      } catch (Exception ex) {
        log.error(
            String.format("Exception while %s is post-handling %s, the error is intentionally bypassed",
                middleware.getClass().getName(), command.getClass().getName()),
            ex);
      }
    }

    if (exceptionHolder.getException() != null) {
      throw exceptionHolder.getException();
    }
    return resultHolder.getResult();
  }
}
