package net.dathoang.lightweightcqrs.commandbus.models;

public class ResultHolder<R> {
  private R result;

  public R getResult() {
    return result;
  }

  public void setResult(R result) {
    this.result = result;
  }
}
