package net.dathoang.cqrs.commandbus.autoscan;

import net.dathoang.cqrs.commandbus.command.Command;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;

@Repeatable(CommandMappings.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMapping {
    Class<? extends Command> value();
}
