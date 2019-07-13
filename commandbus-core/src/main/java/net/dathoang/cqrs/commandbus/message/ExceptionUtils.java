package net.dathoang.cqrs.commandbus.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class ExceptionUtils {

  private ExceptionUtils() {}

  private static final Log log = LogFactory.getLog(ExceptionUtils.class);

  static void callSafely(Runnable function, String errorMessage) {
    try {
      function.run();
    } catch (Exception ex) {
      log.error(errorMessage, ex);
    }
  }
}
