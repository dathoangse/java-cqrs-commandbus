package net.dathoang.cqrs.commandbus.query;

import net.dathoang.cqrs.commandbus.message.Message;

public interface Query<R> extends Message<R> {
}
