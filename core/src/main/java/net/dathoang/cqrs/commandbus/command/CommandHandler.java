package net.dathoang.cqrs.commandbus.command;

public interface CommandHandler<C extends Command<R>, R> {
  R handle(C command) throws Exception;
}
