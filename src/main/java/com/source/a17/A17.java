package com.source.a17;

import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;

import java.util.List;


/**
 * 切面有高级的@Aspect切面和低级的Advisor切面，高级的在执行时也是转换为低级的运行的
 */
public class A17 {
    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        //注册切面类
        context.registerBean("Aspect1", Aspect1.class);
        context.registerBean("Config", Config.class);
        //配置解析@bean注解的后处理器
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean(CommonAnnotationBeanPostProcessor.class);
        //解析@Aspect等注解，进行代理类的创建
        context.registerBean(AnnotationAwareAspectJAutoProxyCreator.class);// 继承自BeanPostProcessor

        context.refresh();

        /*

        实例创建--（代理扩展）依赖注入--初始化（代理扩展）  两个不同的时机进行扩展，二选一
        代理创建时机：
        1.初始化之后(无循环依赖)
        2.实例创建后，依赖注入之前(存在循环依赖)，并暂存于二级缓存


        AnnotationAwareAspectJAutoProxyCreator中的主要功能:
        1.将高级 @Aspect 切面统一为低级 Advisor 切面
        2.在合适的时机创建代理

        有两个主要方法：
        第一个：findEligibleAdvisors 找到有资格的Advisor
               有资格的Advisor 一部分是低级的，可以自己编写，如例子中的Advisor3
               另一部分是高级的，由注解@Aspect进行解析获得

        第二个方法：wrapIfNecessary
                它内部调用 findEligibleAdvisors, 只要返回集合不空, 则表示需要创建代理
                它的调用时机通常在原始对象初始化后执行, 但碰到循环依赖会提前至依赖注入之前执行


        AnnotationAwareAspectJAutoProxyCreator creator =
                context.getBean(AnnotationAwareAspectJAutoProxyCreator.class);

        //方法受保护，调不了......测试findEligibleAdvisors
        List<Advisor> advisors = creator.findEligibleAdvisors(Target1.class, "target1");
        //把所有可以用于Target1的advisor返回
        for (Advisor advisor : advisors) {
            System.out.println(advisor);   //输出4个，一个公共的，一个低级，两个高级
        }

        //方法受保护，调不了......测试wrapIfNecessary
        Object o1 = creator.wrapIfNecessary(new Target1(), "target1", "target1");
        System.out.println(o1.getClass());  //代理对象--有增强
        Object o2 = creator.wrapIfNecessary(new Target2(), "target2", "target2");
        System.out.println(o2.getClass());  //原始对象--无增强
        ((Target1)o1).foo();  //测试是否增强---是

         */

    }


    static class Target1 {
        public void foo() {
            System.out.println("Target1 foo");
        }
    }

    static class Target2 {
        public void bar() {
            System.out.println("Target2 bar");
        }
    }


    @Aspect//高级切面类
    @Order(1)
    static class Aspect1 {

        @Before("execution(* foo())")
        public void before() {
            System.out.println("Aspect1 before");
        }

        @After("execution(* foo())")
        public void after() {
            System.out.println("Aspect1 after");
        }
    }


    @Configuration
    static class Config {
        @Bean//低级的切面
        public Advisor advisor3(MethodInterceptor advice3) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* foo())");
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice3);
            advisor.setOrder(2);
            return advisor;
        }

        @Bean
        public MethodInterceptor advice3() {
            return invocation -> {
                System.out.println("advice3 before");
                Object o = invocation.proceed();
                System.out.println("advice3 after");
                return o;
            };
        }


    }


}
