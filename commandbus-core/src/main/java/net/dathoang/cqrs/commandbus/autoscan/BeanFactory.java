package net.dathoang.cqrs.commandbus.autoscan;

public interface BeanFactory {
  <R> R createBean(Class<R> beanClass);
}
