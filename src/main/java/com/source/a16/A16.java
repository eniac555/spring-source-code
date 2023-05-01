package com.source.a16;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

public class A16 {
    public static void main(String[] args) throws NoSuchMethodException {

        //根据 方法 名 匹配
        AspectJExpressionPointcut pointcut1 = new AspectJExpressionPointcut();
        pointcut1.setExpression("execution(* bar())");
        boolean bar1 = pointcut1.matches(T1.class.getMethod("bar"), T1.class);
        boolean foo1 = pointcut1.matches(T1.class.getMethod("foo"), T1.class);
        System.out.println(bar1);//true
        System.out.println(foo1);//false

        System.out.println("=====================================================");

        //根据 方法 上的 注解 进行匹配
        AspectJExpressionPointcut pointcut2 = new AspectJExpressionPointcut();
        pointcut2.setExpression("@annotation(org.springframework.transaction.annotation.Transactional)");
        boolean bar2 = pointcut2.matches(T1.class.getMethod("bar"), T1.class);
        boolean foo2 = pointcut2.matches(T1.class.getMethod("foo"), T1.class);
        System.out.println(bar2);//false
        System.out.println(foo2);//true

        System.out.println("=====================================================");

        StaticMethodMatcherPointcut pt3 = new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                // 检查方法上是否加了 Transactional 注解
                MergedAnnotations annotations = MergedAnnotations.from(method);
                if (annotations.isPresent(Transactional.class)) {
                    return true;
                }
                // 查看类上是否加了 Transactional 注解
                annotations = MergedAnnotations.from(targetClass,
                        MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
                //SearchStrategy:查找策略，即默认只查一层，其父类或者其实现的接口上加了注解，依然判定本类上没加
                //TYPE_HIERARCHY:从继承树上查找，即考虑分层
                if (annotations.isPresent(Transactional.class)) {
                    return true;
                }
                return false;
            }
        };

        System.out.println(pt3.matches(T1.class.getMethod("foo"), T1.class));
        System.out.println(pt3.matches(T1.class.getMethod("bar"), T1.class));
        System.out.println(pt3.matches(T2.class.getMethod("foo"), T2.class));
        System.out.println(pt3.matches(T3.class.getMethod("foo"), T3.class));

        /*
            总结：
                a. 底层切点实现是如何匹配的: 调用了 aspectj 的匹配方法
                b. 比较关键的是它实现了 MethodMatcher 接口, 用来执行方法的匹配
         */
    }


    static class T1 {
        @Transactional
        public void foo() {
        }

        public void bar() {
        }

    }


    @Transactional
    static class T2 {
        public void foo() {
        }
    }

    @Transactional
    interface I3 {
        void foo();
    }

    static class T3 implements I3 {
        public void foo() {
        }
    }
}
