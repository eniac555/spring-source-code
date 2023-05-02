package com.source.a18;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.*;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class A18 {

    public static void main(String[] args) throws NoSuchMethodException {
        AspectInstanceFactory factory = new SingletonAspectInstanceFactory(new Aspect());

        //1.高级切面类转换为低级切面类
        List<Advisor> list = new ArrayList<>();
        for (Method method : Aspect.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                //解析切点
                String expression = method.getAnnotation(Before.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                //通知类
                AspectJMethodBeforeAdvice advice = new AspectJMethodBeforeAdvice(method, pointcut, factory);
                //切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            } else if (method.isAnnotationPresent(AfterReturning.class)) {
                //解析切点
                String expression = method.getAnnotation(AfterReturning.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                //通知类
                AspectJAfterReturningAdvice advice = new AspectJAfterReturningAdvice(method, pointcut, factory);
                //切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            } else if (method.isAnnotationPresent(Around.class)) {
                //解析切点
                String expression = method.getAnnotation(Around.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                //通知类
                AspectJAroundAdvice advice = new AspectJAroundAdvice(method, pointcut, factory);
                //切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            }
        }

        // 2.转换为低级切面后，所有通知都会转换为环绕通知，这是因为执行循序，参考拦截器的执行过程

        ProxyFactory proxyFactory = new ProxyFactory();
        Target target = new Target();
        proxyFactory.setTarget(target);

        //执行调用链之前，先需要一个暴露调用链的一个环绕通知，把MethodInvocation放入当前线程，就是前面A17中的公共advisor
        //要放在最外层，即所有其他advisor之前
        //proxyFactory.addAdvisor(ExposeInvocationInterceptor.INSTANCE);


        proxyFactory.addAdvisors(list);

        System.out.println("=========================================================");

        //把非环绕转换成环绕
        Method method = Target.class.getMethod("foo");
        List<Object> methodInterceptorList = proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(
                method, Target.class);
        for (Object o : methodInterceptorList) {
            System.out.println(o);
        }

        /*

        其实无论 ProxyFactory 基于哪种方式创建代理，最后下活(调用 advice)的是一个 MethodInvocation 对象
        a，因为 advisor 有多个，且一个套一个调用，因此需要一个调用链对象，即 MethodInvocation
        b. MethodInvocation 要知道 advice 有哪些，还要知道目标， 调用次序如下

        将调用链MethodInvocation 放入当前线程----怎么放？----加入一个额外的环绕通知

        | -> before1 ---------------------------------  从当前线程获取MethodInvocation
        |                                            |
        |   | -> before2 ---------------             |  从当前线程获取MethodInvocation
        |   |                          |             |
        |   |  | -> target ----目标  advice2       advice1
        |   |                          |             |
        |   | -> after2 ----------------             |
        |                                            |
        | -> after1 ----------------------------------

        c. 从上图看，只有环绕通知才适合做advice，所有before，after，afterReturning等都会转换为环绕通知
           ---afterThrowing已经是环绕通知，无需转换
        d. 统一转换为环绕通知，体现了设计模式中的适配器模式
           --对外是为了方便区分before，after，afterReturning
           --对内统一都是环绕通知，统一用 MethodInterceptor表示

         */

        System.out.println("=========================================================");

        for (Advisor advisor : list) {
            System.out.println(advisor);
        }


        //3. 创建并执行调用链(所有环绕通知+目标信息)

        /*

        很好，又是protected方法
        MethodInvocation methodInvocation = new ReflectiveMethodInvocation(
                null, target, method, new Object[0], Target.class, methodInterceptorList);
        methodInvocation.proceed();

        此步骤模拟调用链的过程是一个简单的递归过程：
            1.proceed()方法调用链中的下一个环绕通知
            2.每个环绕通知内部继续调用proceed()
            3.调用到没有其余通知了，就调用目标方法

        */

    }

    static class Aspect {
        @Before("execution(* foo())")
        public void before1() {
            System.out.println("before1");
        }

        @Before("execution(* foo())")
        public void before2() {
            System.out.println("before2");
        }

        @After("execution(* foo())")
        public void after() {
            System.out.println("after");
        }

        @AfterReturning("execution(* foo())")
        public void afterReturning() {
            System.out.println("afterReturning");
        }

        @AfterThrowing("execution(* foo())")
        public void afterThrowing() {
            System.out.println("afterThrowing");
        }

        @Around("execution(* foo())")
        public void around() {
            System.out.println("around");
        }
    }


    static class Target {
        public void foo() {
            System.out.println("target foo");
        }
    }


}
