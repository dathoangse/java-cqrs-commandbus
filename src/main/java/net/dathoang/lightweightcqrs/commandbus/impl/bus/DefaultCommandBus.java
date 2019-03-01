package net.dathoang.lightweightcqrs.commandbus.impl.bus;

import static net.dathoang.lightweightcqrs.commandbus.impl.utils.ExceptionUtils.callSafely;

import net.dathoang.lightweightcqrs.commandbus.exceptions.NoCommandHandlerFoundException;
import net.dathoang.lightweightcqrs.commandbus.interfaces.*;
import net.dathoang.lightweightcqrs.commandbus.models.ResultAndExceptionHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
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
    ResultAndExceptionHolder<R> resultAndExceptionHolder = new ResultAndExceptionHolder<>();
    PipelineContextContainer contextContainer = new DefaultPipelineContextContainer();

    // Pre-handle the command
    for (int i = 0; i < middlewarePipeline.size(); ++i) {
      Middleware middleware = middlewarePipeline.get(i);
      callSafely(() -> MiddlewareContextInjector.injectContext(contextContainer, middleware),
          String.format("Error while injecting contexts into middleware %s", middleware.getClass().getName()));
      try {
        processedMiddlewares.add(middleware);
        middleware.preHandle(command, resultAndExceptionHolder);
        if (resultAndExceptionHolder.getException() != null
            || resultAndExceptionHolder.getResult() != null) {
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
    if (resultAndExceptionHolder.getResult() == null
        && resultAndExceptionHolder.getException() == null) {
      callSafely(() -> MiddlewareContextInjector.injectContext(contextContainer, handler),
          String.format("Error while injecting contexts into handler %s", handler.getClass().getName()));
      try {
        R result = handler.handle(command);
        resultAndExceptionHolder.setResult(result);
      } catch (Exception ex) {
        log.error(
            String.format("Exception while %s is handling %s, the exception will be passed through the bottom to top " +
                    "of the middleware pipeline", handler.getClass().getName(), command.getClass().getName()),
            ex);
        resultAndExceptionHolder.setException(ex);
      }
    }

    // Post-handle the command
    for (int i = processedMiddlewares.size() - 1; i >= 0; --i) {
      Middleware middleware = middlewarePipeline.get(i);
      try {
        middleware.postHandle(command, resultAndExceptionHolder);
      } catch (Exception ex) {
        log.error(
            String.format("Exception while %s is post-handling %s, the error is intentionally bypassed",
                middleware.getClass().getName(), command.getClass().getName()),
            ex);
      }
    }

    if (resultAndExceptionHolder.getException() != null) {
      throw resultAndExceptionHolder.getException();
    }
    return resultAndExceptionHolder.getResult();
  }
}
