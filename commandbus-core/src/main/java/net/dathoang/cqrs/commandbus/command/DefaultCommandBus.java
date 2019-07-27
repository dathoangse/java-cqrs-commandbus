package net.dathoang.cqrs.commandbus.command;

import java.util.List;

import net.dathoang.cqrs.commandbus.exceptions.NoHandlerFoundException;
import net.dathoang.cqrs.commandbus.message.*;
import net.dathoang.cqrs.commandbus.middleware.Middleware;

public final class DefaultCommandBus implements CommandBus {
  private final MessageBus defaultMessageBus;

  public DefaultCommandBus(CommandHandlerFactory commandHandlerFactory,
      List<Middleware> middlewareList) {
    this.defaultMessageBus = new DefaultMessageBus(
        new MessageHandlerFactoryAdapter(commandHandlerFactory), middlewareList
    );
  }

  @Override
  public <R> R dispatch(Command<R> command) throws Exception {
    return defaultMessageBus.dispatch(command);
  }

  static class MessageHandlerFactoryAdapter implements MessageHandlerFactory {

    private final CommandHandlerFactory commandHandlerFactory;

    public MessageHandlerFactoryAdapter(CommandHandlerFactory commandHandlerFactory) {
      this.commandHandlerFactory = commandHandlerFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> MessageHandler<Message<R>, R> createHandler(String messageName) {
      CommandHandler handler = commandHandlerFactory.createCommandHandler(messageName);
      if (handler == null) {
        throw new NoHandlerFoundException(messageName);
      }

      return new MessageHandlerAdapter<>(handler);
    }
  }

  static class MessageHandlerAdapter<M extends Message<R>, R> implements MessageHandler<M, R> {

    private final CommandHandler<Command<R>, R> commandHandler;

    MessageHandlerAdapter(CommandHandler<Command<R>, R> commandHandler) {
      this.commandHandler = commandHandler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R handle(M message) throws Exception {
      return commandHandler.handle((Command<R>)message);
    }
  }
}
