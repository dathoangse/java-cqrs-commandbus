package net.dathoang.lightweightcqrs.commandbus.impl.utils;

import java.util.concurrent.Callable;
import java.util.function.Function;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExceptionUtils {
  private static final Log log = LogFactory.getLog(ExceptionUtils.class);

  public static <R> R callSafely(Callable<R> function, String errorMessage) {
    try {
      return function.call();
    } catch (Exception ex) {
      log.error(errorMessage, ex);
      return null;
    }
  }

  public static void callSafely(Runnable function, String errorMessage) {
    try {
      function.run();
    } catch (Exception ex) {
      log.error(errorMessage, ex);
    }
  }
}
