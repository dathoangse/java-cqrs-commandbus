package net.dathoang.cqrs.commandbus.command;

public interface CommandHandlerFactory {
  <R> CommandHandler<Command<R>, R> createHandler(String commandName);
}
