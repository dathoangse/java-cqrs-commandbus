package net.dathoang.cqrs.commandbus.query;

import java.util.List;
import net.dathoang.cqrs.commandbus.middleware.Middleware;

public class QueryBusFactory {
  public static QueryBus create(QueryHandlerFactory queryHandlerFactory,
      List<Middleware> middlewareList) {
    return new DefaultQueryBus(queryHandlerFactory, middlewareList);
  }
}
