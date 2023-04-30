package com.source.a11;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

public class CglibProxyDemo {

    static class Target {
        public void foo() {
            System.out.println("target foo");
        }
    }


    // 代理是子类型, 目标是父类型
    public static void main(String[] param) {
        Target target = new Target();


        //参数1：指明代理的父类
        Target proxy = (Target) Enhancer.create(Target.class,
                (MethodInterceptor) (p, method, args, methodProxy) -> {
            //p, method, args, methodProxy  代理对象本身，当前代理类中执行的方法，参数列表，方法的代理
            System.out.println("before...");
            Object result1 = method.invoke(target, args); // 用方法反射调用目标
            //methodProxy 它可以避免反射调用
            Object result2 = methodProxy.invoke(target, args); // 内部没有用反射, 需要目标 （spring用的这种）
            Object result = methodProxy.invokeSuper(p, args); // 内部没有用反射, 需要代理
            System.out.println("after...");
            return result;
        });

        proxy.foo();

    }
}