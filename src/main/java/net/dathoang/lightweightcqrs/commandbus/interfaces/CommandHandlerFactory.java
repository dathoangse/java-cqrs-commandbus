package net.dathoang.lightweightcqrs.commandbus.interfaces;

public interface CommandHandlerFactory {
  <R> CommandHandler<Command<R>, R> createHandler(String commandName);
}
