package net.dathoang.lightweightcqrs.commandbus;

import java.util.concurrent.Callable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class ExceptionUtils {

  private ExceptionUtils() {}

  private static final Log log = LogFactory.getLog(ExceptionUtils.class);

  static <R> R callSafely(Callable<R> function, String errorMessage) {
    try {
      return function.call();
    } catch (Exception ex) {
      log.error(errorMessage, ex);
      return null;
    }
  }

  static void callSafely(Runnable function, String errorMessage) {
    try {
      function.run();
    } catch (Exception ex) {
      log.error(errorMessage, ex);
    }
  }
}
