package net.dathoang.cqrs.commandbus;

public interface CommandHandlerFactory {
  <R> CommandHandler<Command<R>, R> createHandler(String commandName);
}
