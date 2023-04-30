package com.source.a07;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/*
    bean 初始化和销毁的执行顺序
    1.后处理器的初始化 2.InitializingBean的初始化  3.@Bean的初始化

    1.@PreDestroy，也就是扩展功能的销毁   2.DisposableBean接口的销毁  3.@Bean的销毁

    aware接口相关的初始化，在1和2之间进行
 */

@SpringBootApplication
public class A07_1 {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(A07_1.class, args);
        context.close();
        /*
            总结一下？？？
                a. Spring 提供了多种初始化和销毁手段
                b. Spring 的面试有多么地卷......
         */
    }

    @Bean(initMethod = "init3")
    public Bean1 bean1() {
        return new Bean1();
    }

    @Bean(destroyMethod = "destroy3")
    public Bean2 bean2() {
        return new Bean2();
    }
}
