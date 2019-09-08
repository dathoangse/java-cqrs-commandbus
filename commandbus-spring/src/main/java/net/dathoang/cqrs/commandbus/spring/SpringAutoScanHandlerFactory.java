package net.dathoang.cqrs.commandbus.spring;

import net.dathoang.cqrs.commandbus.autoscan.AutoScanHandlerFactory;
import net.dathoang.cqrs.commandbus.autoscan.BeanFactory;
import net.dathoang.cqrs.commandbus.autoscan.HandlerScan;
import org.springframework.context.ApplicationContext;

import java.util.*;

public class SpringAutoScanHandlerFactory extends AutoScanHandlerFactory {

  private ApplicationContext context;

  public SpringAutoScanHandlerFactory(ApplicationContext context) {
    super(new BeanFactory() {
      @Override
      public <R> R createBean(Class<R> beanClass) {
        context.getAutowireCapableBeanFactory().autowireBean(beanClass);
        return context.getAutowireCapableBeanFactory().createBean(beanClass);
      }
    });
    this.context = context;

    Set<String> packagesToScan = getPackagesToScanConfig();
    packagesToScan.forEach(this::scanAndRegisterHandlers);
  }

  private Set<String> getPackagesToScanConfig() {
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
