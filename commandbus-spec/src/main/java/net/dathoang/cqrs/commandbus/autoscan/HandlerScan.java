package net.dathoang.cqrs.commandbus.autoscan;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerScan {
  String[] basePackages() default {};
}
