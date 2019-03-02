package net.dathoang.cqrs.commandbus;

public interface CommandHandler<C extends Command<R>, R> {
  R handle(C command) throws Exception;
}
