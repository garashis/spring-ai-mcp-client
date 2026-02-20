package com.ai.mcp.client;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

//@Component
public class BeanViewer {

    private final ApplicationContext applicationContext;

    public BeanViewer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public void printBeans() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        Arrays.sort(beanNames);

        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            System.out.println(beanName + " -> " + bean.getClass().getName());
        }
    }
}
