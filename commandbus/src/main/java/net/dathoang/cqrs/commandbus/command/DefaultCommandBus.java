package net.dathoang.cqrs.commandbus.command;

import java.util.List;
import net.dathoang.cqrs.commandbus.command.Command;
import net.dathoang.cqrs.commandbus.command.CommandBus;
import net.dathoang.cqrs.commandbus.command.CommandHandler;
import net.dathoang.cqrs.commandbus.command.CommandHandlerFactory;
import net.dathoang.cqrs.commandbus.exceptions.InvalidMessageTypeException;
import net.dathoang.cqrs.commandbus.message.Message;
import net.dathoang.cqrs.commandbus.message.MessageBus;
import net.dathoang.cqrs.commandbus.message.MessageBusFactory;
import net.dathoang.cqrs.commandbus.message.MessageHandler;
import net.dathoang.cqrs.commandbus.message.MessageHandlerFactory;
import net.dathoang.cqrs.commandbus.middleware.Middleware;

final class DefaultCommandBus implements CommandBus {
  private final MessageBus defaultMessageBus;

  public DefaultCommandBus(CommandHandlerFactory commandHandlerFactory,
      List<Middleware> middlewareList) {
    this.defaultMessageBus = MessageBusFactory.create(
        new MessageHandlerFactoryAdapter(commandHandlerFactory), middlewareList
    );
  }

  @Override
  public <R> R dispatch(Command<R> command) throws Exception {
    return defaultMessageBus.dispatch(command);
  }

  // region adapter classes
  class MessageHandlerFactoryAdapter implements MessageHandlerFactory {

    private final CommandHandlerFactory commandHandlerFactory;

    public MessageHandlerFactoryAdapter(CommandHandlerFactory commandHandlerFactory) {
      this.commandHandlerFactory = commandHandlerFactory;
    }

    @Override
    public <R> MessageHandler<Message<R>, R> createHandler(String messageName) {
      return new MessageHandlerAdapter<>(
          commandHandlerFactory.createHandler(messageName)
      );
    }
  }

  class MessageHandlerAdapter<M extends Message<R>, R> implements MessageHandler<M, R> {

    private final CommandHandler<Command<R>, R> commandHandler;

    MessageHandlerAdapter(CommandHandler<Command<R>, R> commandHandler) {
      this.commandHandler = commandHandler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R handle(M message) throws Exception {
      Command<R> command;
      try {
        command = (Command<R>)message;
      } catch (ClassCastException ex) {
        throw new InvalidMessageTypeException(
            String.format("The message %s passed to command handler %s is not a command",
                message.getClass().getName(), commandHandler.getClass().getName()),
            ex);
      }
      return commandHandler.handle(command);
    }
  }
  // endregion
}
