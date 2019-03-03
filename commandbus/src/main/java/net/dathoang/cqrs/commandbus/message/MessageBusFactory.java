package net.dathoang.cqrs.commandbus.message;

import java.util.List;
import net.dathoang.cqrs.commandbus.middleware.Middleware;

public class MessageBusFactory {
  public static MessageBus create(MessageHandlerFactory messageHandlerFactory,
                                  List<Middleware> middlewareList) {
    return new DefaultMessageBus(messageHandlerFactory, middlewareList);
  }
}
