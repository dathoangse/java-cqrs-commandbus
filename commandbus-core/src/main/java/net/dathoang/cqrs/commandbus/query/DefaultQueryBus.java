package net.dathoang.cqrs.commandbus.query;

import java.util.List;

import net.dathoang.cqrs.commandbus.exceptions.NoHandlerFoundException;
import net.dathoang.cqrs.commandbus.message.Message;
import net.dathoang.cqrs.commandbus.message.MessageBus;
import net.dathoang.cqrs.commandbus.message.MessageBusFactory;
import net.dathoang.cqrs.commandbus.message.MessageHandler;
import net.dathoang.cqrs.commandbus.message.MessageHandlerFactory;
import net.dathoang.cqrs.commandbus.middleware.Middleware;

public final class DefaultQueryBus implements QueryBus {

  private final MessageBus defaultMessageBus;

  public DefaultQueryBus(QueryHandlerFactory queryHandlerFactory, List<Middleware> middlewareList) {
    defaultMessageBus = MessageBusFactory.create(
        new QueryHandlerFactoryToMessageHandlerFactoryAdapter(queryHandlerFactory),
        middlewareList
    );
  }

  @Override
  public <R> R dispatch(Query<R> query) throws Exception {
    return defaultMessageBus.dispatch(query);
  }

  // region adapter classes
  static class QueryHandlerFactoryToMessageHandlerFactoryAdapter implements MessageHandlerFactory {

    private final QueryHandlerFactory queryHandlerFactory;

    public QueryHandlerFactoryToMessageHandlerFactoryAdapter(
        QueryHandlerFactory queryHandlerFactory) {
      this.queryHandlerFactory = queryHandlerFactory;
    }

    @Override
    public <R> MessageHandler<Message<R>, R> createHandler(String messageName) {
      QueryHandler queryHandler = queryHandlerFactory.createQueryHandler(messageName);
      if (queryHandler == null) {
        throw new NoHandlerFoundException(messageName);
      }

      return new QueryHandlerToMessageHandlerAdapter<>(queryHandler);
    }
  }

  static class QueryHandlerToMessageHandlerAdapter<M extends Message<R>, R>
      implements MessageHandler<M, R> {

    private final QueryHandler<Query<R>, R> queryHandler;

    QueryHandlerToMessageHandlerAdapter(QueryHandler<Query<R>, R> queryHandler) {
      this.queryHandler = queryHandler;
    }

    @Override
    public R handle(M message) throws Exception {
      return queryHandler.handle(castToQuery(message));
    }

    @SuppressWarnings("unchecked")
    private Query<R> castToQuery(Message<R> message) {
      return (Query<R>) message;
    }
  }
  // endregion
}
