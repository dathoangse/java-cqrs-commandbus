package net.dathoang.cqrs.commandbus.query;

public interface QueryHandlerFactory {
  <R> QueryHandler<Query<R>, R> createQueryHandler(String queryName);
}
