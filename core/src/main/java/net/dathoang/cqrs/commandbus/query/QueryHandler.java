package net.dathoang.cqrs.commandbus.query;

import net.dathoang.cqrs.commandbus.message.MessageHandler;

public interface QueryHandler<Q extends Query<R>, R> extends MessageHandler<Q, R> {
  R handle(Q query) throws Exception;
}
