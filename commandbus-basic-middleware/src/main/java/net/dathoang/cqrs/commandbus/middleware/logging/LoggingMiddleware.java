package net.dathoang.cqrs.commandbus.middleware.logging;

import net.dathoang.cqrs.commandbus.command.Command;
import net.dathoang.cqrs.commandbus.message.Message;
import net.dathoang.cqrs.commandbus.middleware.Middleware;
import net.dathoang.cqrs.commandbus.middleware.ResultAndExceptionHolder;
import net.dathoang.cqrs.commandbus.query.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggingMiddleware implements Middleware {
  private static final Log log = LogFactory.getLog(LoggingMiddleware.class);

  @Override
  public <R> void preHandle(Message<R> message,
      ResultAndExceptionHolder<R> resultAndExceptionHolder) {
    log.info(String.format("Received %s (%s), the %s has been dispatched to %s bus",
        message.getClass().getName(), message.toString(), getMessageType(message),
        getMessageType(message)));
  }

  @Override
  public <R> void postHandle(Message<R> message,
      ResultAndExceptionHolder<R> resultAndExceptionHolder) {
    if (resultAndExceptionHolder.getException() != null) {
      log.error(String.format("Failed to handle %s (%s)",
          message.getClass().getName(), message.toString()),
          resultAndExceptionHolder.getException());
    } else {
      String resultInStr = resultAndExceptionHolder.getResult() != null
          ? resultAndExceptionHolder.getResult().toString() : "null";
      log.info(String.format("The %s %s (%s) has been handled successfully with result: %s",
          getMessageType(message), message.getClass().getName(), message.toString(), resultInStr));
    }
  }

  private String getMessageType(Message<?> message) {
    if (message instanceof Command<?>) {
      return "command";
    } else if (message instanceof Query<?>) {
      return "query";
    } else {
      return "message";
    }
  }
}
