package com.source.a02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.Resource;

public class TestBeanFactory {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // bean 的定义（class, scope, 初始化, 销毁）
        AbstractBeanDefinition beanDefinition =
                BeanDefinitionBuilder.genericBeanDefinition(Config.class)
                        .setScope("singleton").getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);

        // 给 BeanFactory 添加一些常用的后处理器，把bean处理器添加到BeanFactory，能够解析@Configuration和@Bean
        // 此时的后处理器只是存在于BeanFactory中的一个bean
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);

        // BeanFactory 后处理器主要功能，补充了一些 bean 定义
        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class)//拿到所有的bean工厂后处理器
                .values()
                .forEach(beanFactoryPostProcessor -> {
                    beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
                });

        // Bean 后处理器, 针对 bean 的生命周期的各个阶段提供扩展, 例如 @Autowired @Resource ...
        // 此时是建立BeanFactory和后处理器之间的联系
        beanFactory.getBeansOfType(BeanPostProcessor.class)//拿到所有的bean后处理器
                .values().stream()
                .sorted(beanFactory.getDependencyComparator())
                //.sorted(Objects.requireNonNull(beanFactory.getDependencyComparator()))
                //加比较器，设置后处理器顺序，由此确定不同比较器之间的解析顺序
                .forEach(beanPostProcessor -> {
                    System.out.println(">>>>" + beanPostProcessor);
                    beanFactory.addBeanPostProcessor(beanPostProcessor);
                });

        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        beanFactory.preInstantiateSingletons(); // 准备好所有单例，不然就是用到时才会构造
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        //System.out.println(beanFactory.getBean(Bean1.class).getBean2());
        System.out.println("======" + beanFactory.getBean(Bean1.class).getInter());

        /*

            学到了什么:
            a. beanFactory 不会做的事
                   1. 不会主动调用 BeanFactory 后处理器
                   2. 不会主动添加 Bean 后处理器
                   3. 不会主动初始化单例
                   4. 不会解析beanFactory 还不会解析 ${ } 与 #{ }
                   but，ApplicationContext 把上面这些都做好了
            b. bean 后处理器会有排序的逻辑

         */

        System.out.println("Common:" + (Ordered.LOWEST_PRECEDENCE - 3));
        System.out.println("Autowired:" + (Ordered.LOWEST_PRECEDENCE - 2));
        //Ordered：Java中用于排序的接口
        //小的排在前面，即有较高的优先级
    }

    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }

        @Bean
        public Bean3 bean3() {
            return new Bean3();
        }

        @Bean
        public Bean4 bean4() {
            return new Bean4();
        }
    }


    interface Inter {

    }


    static class Bean3 implements Inter {

    }


    static class Bean4 implements Inter {

    }


    static class Bean1 {
        private static final Logger log = LoggerFactory.getLogger(Bean1.class);

        public Bean1() {
            log.debug("构造 Bean1()");
        }

        @Autowired
        private Bean2 bean2;

        public Bean2 getBean2() {
            return bean2;
        }


        //同时加这两个，默认Autowired先生效
        //可以在加Bean 后处理器时通过比较器设置优先级让Resource先生效
        @Autowired
        @Resource(name = "bean4")//注入bean4
        private Inter bean3;

        public Inter getInter() {
            return bean3;
        }
    }


    static class Bean2 {
        private static final Logger log = LoggerFactory.getLogger(Bean2.class);

        public Bean2() {
            log.debug("构造 Bean2()");
        }
    }
}
