package net.dathoang.cqrs.commandbus.query;

interface QueryHandler<Q extends Query<R>, R> {
  R handle(Q query) throws Exception;
}
