package net.dathoang.lightweightcqrs.commandbus.interfaces;

public interface PureCommandHandler<C extends Command<Void>> extends CommandHandler<C, Void> {
}
