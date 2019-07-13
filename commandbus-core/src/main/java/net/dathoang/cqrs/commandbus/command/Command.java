package net.dathoang.cqrs.commandbus.command;

import net.dathoang.cqrs.commandbus.message.Message;

public interface Command<R> extends Message<R> {
}
