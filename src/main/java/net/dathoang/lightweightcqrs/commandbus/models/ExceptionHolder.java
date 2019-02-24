package net.dathoang.lightweightcqrs.commandbus.models;

public class ExceptionHolder {
  private Exception exception;

  public Exception getException() {
    return exception;
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }
}
