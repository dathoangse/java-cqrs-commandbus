package net.dathoang.cqrs.commandbus.middleware;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link MiddlewareContext} is a marker annotation to tell middleware pipeline to inject the
 * context. The behavior is the same as @Context annotation, the only difference is that this
 * context is provided by outer middleware in the pipeline instead of by the DI framework you're
 * using.
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MiddlewareContext {

}
