package net.dathoang.cqrs.commandbus.command;

import net.dathoang.cqrs.commandbus.message.MessageHandler;

public interface CommandHandler<C extends Command<R>, R> extends MessageHandler<C, R> {
  R handle(C command) throws Exception;
}