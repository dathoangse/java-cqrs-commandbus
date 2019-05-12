package net.dathoang.cqrs.commandbus.query;

public interface QueryBus {
  <R> R dispatch(Query<R> query) throws Exception;
}
