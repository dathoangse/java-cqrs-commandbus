package net.dathoang.cqrs.commandbus.autoscan;

import net.dathoang.cqrs.commandbus.query.Query;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface QueryMapping {
  Class<? extends Query> value();
}
