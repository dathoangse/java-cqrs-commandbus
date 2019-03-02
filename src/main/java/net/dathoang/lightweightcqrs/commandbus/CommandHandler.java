package net.dathoang.lightweightcqrs.commandbus;

public interface CommandHandler<C extends Command<R>, R> {
  R handle(C command) throws Exception;
}
