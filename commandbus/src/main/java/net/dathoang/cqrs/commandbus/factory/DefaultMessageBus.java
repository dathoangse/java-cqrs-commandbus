package net.dathoang.cqrs.commandbus.factory;

import static net.dathoang.cqrs.commandbus.factory.ExceptionUtils.callSafely;

import net.dathoang.cqrs.commandbus.exceptions.NoHandlerFoundException;
import net.dathoang.cqrs.commandbus.message.Message;
import net.dathoang.cqrs.commandbus.middleware.Middleware;
import net.dathoang.cqrs.commandbus.middleware.PipelineContextContainer;
import net.dathoang.cqrs.commandbus.middleware.ResultAndExceptionHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

final class DefaultMessageBus implements MessageBus {
  private static final Log log = LogFactory.getLog(DefaultMessageBus.class);

  private final List<Middleware> middlewarePipeline;
  private MessageHandlerFactory messageHandlerFactory;

  DefaultMessageBus(MessageHandlerFactory handlerFactory, List<Middleware> middlewareList) {
    messageHandlerFactory = handlerFactory;
    middlewarePipeline = new ArrayList<>(middlewareList);
  }

  /**
   * Find a {@link MessageHandler} that can handle this {@link Message} and dispatch it to the handler
   *
   * @param message the message to dispatch
   * @param <R> the type of the result produced after handling the message
   * @return the result after handling the message
   * @throws NoHandlerFoundException when the {@link MessageBus} can't find corresponding
   *         {@link MessageHandler} for the {@link Message}.
   * @throws Exception
   */
  @Override
  public <R> R dispatch(Message<R> message) throws Exception {
    MessageHandler<Message<R>, R> messageHandler = messageHandlerFactory
        .createHandler(message.getClass().getName());
    if (messageHandler == null) {
      throw new NoHandlerFoundException(message.getClass());
    }
    return dispatchThroughMiddlewarePipeline(message, messageHandler);
  }

  private <R> R dispatchThroughMiddlewarePipeline(Message<R> message, MessageHandler<Message<R>, R> messageHandler) throws Exception {
    List<Middleware> processedMiddlewares = new ArrayList<>();
    ResultAndExceptionHolder<R> resultAndExceptionHolder = new ResultAndExceptionHolder<>();
    PipelineContextContainer contextContainer = new DefaultPipelineContextContainer();

    // Pre-handle the message
    for (int i = 0; i < middlewarePipeline.size(); ++i) {
      Middleware middleware = middlewarePipeline.get(i);
      callSafely(() -> MiddlewareContextInjector.injectContext(contextContainer, middleware),
          String.format("Error while injecting contexts into middleware %s", middleware.getClass().getName()));
      try {
        processedMiddlewares.add(middleware);
        middleware.preHandle(message, resultAndExceptionHolder);
        if (resultAndExceptionHolder.getException() != null
            || resultAndExceptionHolder.getResult() != null) {
          break;
        }
      } catch (Exception ex) {
        log.error(
            String.format("Exception while %s is pre-handling %s, the error is intentionally bypassed",
                middleware.getClass().getName(), message.getClass().getName()),
            ex);
      }
    }

    // Handle the message
    if (resultAndExceptionHolder.getResult() == null
        && resultAndExceptionHolder.getException() == null) {
      callSafely(() -> MiddlewareContextInjector.injectContext(contextContainer, messageHandler),
          String.format("Error while injecting contexts into messageHandler %s", messageHandler.getClass().getName()));
      try {
        R result = messageHandler.handle(message);
        resultAndExceptionHolder.setResult(result);
      } catch (Exception ex) {
        log.error(
            String.format("Exception while %s is handling %s, the exception will be passed through the bottom to top " +
                    "of the middleware pipeline", messageHandler.getClass().getName(), message.getClass().getName()),
            ex);
        resultAndExceptionHolder.setException(ex);
      }
    }

    // Post-handle the message
    for (int i = processedMiddlewares.size() - 1; i >= 0; --i) {
      Middleware middleware = middlewarePipeline.get(i);
      try {
        middleware.postHandle(message, resultAndExceptionHolder);
      } catch (Exception ex) {
        log.error(
            String.format("Exception while %s is post-handling %s, the error is intentionally bypassed",
                middleware.getClass().getName(), message.getClass().getName()),
            ex);
      }
    }

    if (resultAndExceptionHolder.getException() != null) {
      throw resultAndExceptionHolder.getException();
    }
    return resultAndExceptionHolder.getResult();
  }
}
