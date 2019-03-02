package net.dathoang.cqrs.commandbus.factory;

import java.util.List;
import net.dathoang.cqrs.commandbus.exceptions.InvalidMessageTypeException;
import net.dathoang.cqrs.commandbus.message.Message;
import net.dathoang.cqrs.commandbus.middleware.Middleware;
import net.dathoang.cqrs.commandbus.query.Query;
import net.dathoang.cqrs.commandbus.query.QueryBus;
import net.dathoang.cqrs.commandbus.query.QueryHandler;
import net.dathoang.cqrs.commandbus.query.QueryHandlerFactory;

final class DefaultQueryBus implements QueryBus {

  private final DefaultMessageBus defaultMessageBus;

  public DefaultQueryBus(QueryHandlerFactory queryHandlerFactory, List<Middleware> middlewareList) {
    defaultMessageBus = new DefaultMessageBus(
        new QueryHandlerFactoryToMessageHandlerFactoryAdapter(queryHandlerFactory),
        middlewareList
    );
  }

  @Override
  public <R> R dispatch(Query<R> query) throws Exception {
    return defaultMessageBus.dispatch(query);
  }

  // region adapter classes
  class QueryHandlerFactoryToMessageHandlerFactoryAdapter implements MessageHandlerFactory {

    private final QueryHandlerFactory queryHandlerFactory;

    public QueryHandlerFactoryToMessageHandlerFactoryAdapter(QueryHandlerFactory queryHandlerFactory) {
      this.queryHandlerFactory = queryHandlerFactory;
    }

    @Override
    public <R> MessageHandler<Message<R>, R> createHandler(String messageName) {
      return new QueryHandlerToMessageHandlerAdapter<>(
          queryHandlerFactory.createHandler(messageName)
      );
    }
  }

  class QueryHandlerToMessageHandlerAdapter<M extends Message<R>, R> implements MessageHandler<M, R> {

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
      Query<R> query;
      try {
        query = (Query<R>)message;
      } catch (ClassCastException ex) {
        throw new InvalidMessageTypeException(
            String.format("The message %s passed to command handler %s is not a command",
                message.getClass().getName(), queryHandler.getClass().getName()),
            ex);
      }
      return query;
    }
  }
  // endregion
}
