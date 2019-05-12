package net.dathoang.cqrs.commandbus.query;

public interface QueryHandler<Q extends Query<R>, R> {
  R handle(Q query) throws Exception;
}
