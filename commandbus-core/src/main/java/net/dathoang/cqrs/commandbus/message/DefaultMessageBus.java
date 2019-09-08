package net.dathoang.cqrs.commandbus.message;

import net.dathoang.cqrs.commandbus.exceptions.NoHandlerFoundException;
import net.dathoang.cqrs.commandbus.middleware.Middleware;
import net.dathoang.cqrs.commandbus.middleware.NextMiddlewareFunction;

import java.util.ArrayList;
import java.util.List;

public final class DefaultMessageBus implements MessageBus {
  private static final int FIRST_MIDDLEWARE_INDEX = 0;

  private MessageHandlerFactory messageHandlerFactory;
  private final List<Middleware> middlewarePipeline;

  public DefaultMessageBus(MessageHandlerFactory handlerFactory, List<Middleware> middlewareList) {
    messageHandlerFactory = handlerFactory;
    middlewarePipeline = new ArrayList<>(middlewareList);
  }

  /**
   * Find a {@link MessageHandler} that can handle this {@link Message} and dispatch it to the
   * handler.
   *
   * @param message the message to dispatch
   * @param <R> the type of the result produced after handling the message
   * @return the result after handling the message
   * @throws NoHandlerFoundException when the {@link MessageBus} can't find corresponding
   *         {@link MessageHandler} for the {@link Message}.
   * @throws Exception possibly raised by OldMiddleware or MessageHandler
   */
  @Override
  @SuppressWarnings("unchecked")
  public <R> R dispatch(Message<R> message) throws Exception {
    return (R)getNext(FIRST_MIDDLEWARE_INDEX).call((Message<Object>)message);
  }

  private <R> NextMiddlewareFunction<Message<R>, R> getNext(int nextMiddlewareIndex) {
    if (nextMiddlewareIndex < middlewarePipeline.size()) {
      // Handle using next middleware
      return (message) -> {
        Middleware nextMiddleware = middlewarePipeline.get(nextMiddlewareIndex);
        return nextMiddleware.handle(message, getNext(nextMiddlewareIndex + 1));
      };
    } else {
      // Handle using message handler
      return (message) -> {
        MessageHandler<Message<R>, R> handler = messageHandlerFactory.createHandler(message.getClass().getName());
        if (handler == null) {
          throw new NoHandlerFoundException(message.getClass());
        }
        return handler.handle(message);
      };
    }
  }
}
