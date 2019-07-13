package net.dathoang.cqrs.commandbus.middleware;

/**
 * {@link PipelineContextContainer} is used to store:
 * - Middleware's data per command. (mainly to store and pass data between Middleware.preHandle()
 *   call and Middleware.postHandle() call)
 * - Context injected from outer middleware (Outer middleware can inject contexts into inner
 *   middleware or into command handler)
 */
public interface PipelineContextContainer {

  /**
   * Get the handling data of the middleware for the current command.
   * @param middlewareClass the class of the current middleware (for name-spacing)
   * @param key the key of the data
   * @return the data at key key for the middleware middlewareClass
   */
  Object getMiddlewareData(Class<? extends Middleware> middlewareClass, String key);

  /**
   * Set the handling data of the middleware for the current command.
   * @param middlewareClass the class of the current middleware (for name-spacing)
   * @param key the key of the data
   * @param value the value of he data
   */
  void setMiddlewareData(Class<? extends Middleware> middlewareClass, String key, Object value);

  /**
   * Set the context instance to be injected into inner middleware in the pipeline
   * or into the command handler.
   * The current middleware's context injection supports:
   * - Method injection
   * - Field injection
   * The middleware's context injection behavior is the same as @Context annotation, the only
   * different is that this context is provided by outer middleware in the pipeline.
   * And there will be no conflicting with the current (C)DI framework, as we will use
   * {@link MiddlewareContext} annotation instead of @Context annotation.
   *
   * @param contextClass the class type of the context.
   * @param instance the context instance which will be provided to the inner middleware and command
   *        handler.
   * @param <R> the type of the context.
   */
  <R> void bindContext(Class<R> contextClass, R instance);

  /**
   * Resolve the injected context.
   *
   * @see #bindContext(Class, Object)
   * @param contextClass the class type of the dependency
   * @return the injected dependency
   */
  Object resolveContext(Class contextClass);
}
