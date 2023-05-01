package com.source.a12;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

//真实的jdk代理类源码和这个基本一样，真实源码可以通过Arthas插件反编译生成

//jdk代理字节码可以用ASM插件生成

//代理与目标实现共同的接口
public class $Proxy0 extends Proxy implements A12.Foo {

    //之所以没有成员变量是因为父类Proxy中已经有了成员变量 InvocationHandler h


    //构造方法
    public $Proxy0(InvocationHandler h) {
        super(h);
    }

    @Override
    public void foo() {
        try {
            h.invoke(this, foo, new Object[0]);
        } catch (RuntimeException | Error e) {
            //运行异常直接抛出
            throw e;
        } catch (Throwable e) {
            //检查异常，转换后抛出
            throw new UndeclaredThrowableException(e);
        }
    }


    @Override
    public int bar() {
        try {
            Object result = h.invoke(this, bar, new Object[0]);
            return (int) result;
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    static Method foo;
    static Method bar;

    static {
        try {
            foo = A12.Foo.class.getMethod("foo");
            bar = A12.Foo.class.getMethod("bar");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }
}
