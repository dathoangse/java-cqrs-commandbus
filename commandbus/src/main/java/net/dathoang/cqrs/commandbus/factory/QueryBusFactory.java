package net.dathoang.cqrs.commandbus.factory;

import java.util.List;
import net.dathoang.cqrs.commandbus.middleware.Middleware;
import net.dathoang.cqrs.commandbus.query.QueryBus;
import net.dathoang.cqrs.commandbus.query.QueryHandlerFactory;

public class QueryBusFactory {
  public static QueryBus create(QueryHandlerFactory queryHandlerFactory, List<Middleware> middlewareList) {
    return new DefaultQueryBus(queryHandlerFactory, middlewareList);
  }
}
