package net.dathoang.lightweightcqrs.commandbus;

public interface CommandHandlerFactory {
  <R> CommandHandler<Command<R>, R> createHandler(String commandName);
}
