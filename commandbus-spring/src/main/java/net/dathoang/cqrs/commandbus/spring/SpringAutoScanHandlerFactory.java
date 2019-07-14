package net.dathoang.cqrs.commandbus.spring;

import net.dathoang.cqrs.commandbus.autoscan.AutoScanHandlerFactory;
import net.dathoang.cqrs.commandbus.autoscan.HandlerScan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.function.Function;

public class SpringAutoScanHandlerFactory extends AutoScanHandlerFactory {

  private final ApplicationContext context;

  public SpringAutoScanHandlerFactory(ApplicationContext context) {
    this.context = context;

    this.startScanningHandler();
  }

  @Override
  protected Function<Class, Object> getBeanFactory() {
    return (cls) -> {
      context.getAutowireCapableBeanFactory().autowireBean(cls);
      return context.getAutowireCapableBeanFactory().createBean(cls);
    };
  }

  @Override
  protected Set<String> getPackagesToScanConfig() {
    Set<String> packagesToScan = new HashSet<>();
    Map<String, Object> springApplicationInstances = context.getBeansWithAnnotation(HandlerScan.class);
    for (Object appInstance : springApplicationInstances.values()) {
      HandlerScan scanAnnotation = appInstance.getClass().getAnnotation(HandlerScan.class);
      if (scanAnnotation != null) {
        packagesToScan.addAll(Arrays.asList(scanAnnotation.basePackages()));
      }
    }
    return packagesToScan;
  }
}
