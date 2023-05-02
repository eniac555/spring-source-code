package com.source.a19;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;

public class A19 {
    @Aspect
    static class MyAspect {

        @Before("execution(* foo(..))")//静态通知调用，没有参数绑定，执行时不需要切点
        public void before1() {
            System.out.println("before1");
        }

        @Before("execution(* foo(..)) && args(x)")//动态通知调用--需要参数绑定，执行时需要切点
        public void before2(int x) {
            System.out.printf("before2(%d)%n", x);
        }
    }


    static class Target {
        public void foo(int x) {
            System.out.printf("target foo(%d)%n", x);
        }
    }


    @Configuration
    static class MyConfig {
        @Bean
        AnnotationAwareAspectJAutoProxyCreator proxyCreator() {
            return new AnnotationAwareAspectJAutoProxyCreator();
        }

        @Bean
        public MyAspect myAspect() {
            return new MyAspect();
        }
    }

    public static void main(String[] args) throws Throwable {

        /*

        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean(MyConfig.class);
        context.refresh();

        AnnotationAwareAspectJAutoProxyCreator creator = context.getBean(AnnotationAwareAspectJAutoProxyCreator.class);
        //获得所有的低级切面Advisor--环绕的
        List<Advisor> list = creator.findEligibleAdvisors(Target.class, "target");
        //还是说这个方法受保护，无法直接调用

        Target target = new Target();
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(target);
        factory.addAdvisors(list);
        //获取代理
        Object proxy = factory.getProxy();

        List<Object> objectList = factory.getInterceptorsAndDynamicInterceptionAdvice(
                Target.class.getMethod("foo", int.class), Target.class);
        for (Object o : objectList) {
            System.out.println(o);
        }

        ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(proxy, target,
                Target.class.getMethod("foo", int.class),
                new Object[]{10}, Target.class, objectList) {};//方法受保护，{}创建他的子类

        invocation.proceed();

        */

        
        /*

        总结:
            有参数绑定的通知，在调用时依然需要切点，对参数进行匹配及绑定
            动态调用复杂程度高，性能比无参数绑定的低很多

         */

    }


}
