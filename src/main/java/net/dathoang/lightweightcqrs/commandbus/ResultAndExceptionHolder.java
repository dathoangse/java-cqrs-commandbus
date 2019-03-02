package net.dathoang.lightweightcqrs.commandbus;

public final class ResultAndExceptionHolder<R> {
  private Exception exception;
  private R result;

  public Exception getException() {
    return exception;
  }

  public void setException(Exception exception) {
    this.exception = exception;
    this.result = null;
  }

  public R getResult() {
    return result;
  }

  public void setResult(R result) {
    this.result = result;
    this.exception = null;
  }
}
